#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <errno.h>

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>

#include <pthread.h>

#include "Packet.h"
#include "constants.h"

// Transmission management
int seq_number_counter = -1;
int slots_available;
PACKET window[WIN_DIMENSION];

// Thread management
int return_code_send_thread;
pthread_mutex_t mutex_array[WIN_DIMENSION];
pthread_cond_t  condition_array[WIN_DIMENSION];
pthread_mutex_t mutex_seq_number = PTHREAD_MUTEX_INITIALIZER;

// Protoypes
void *manage_packet(void *packet);
void invia_pacchetto(PACKET *packet);

void init_transmission(){
	int i;

	for(i = 0; i < WIN_DIMENSION; i++){
		pthread_mutex_t mut = PTHREAD_MUTEX_INITIALIZER;
		pthread_cond_t cond = PTHREAD_COND_INITIALIZER;

		mutex_array[i] = mut;
		condition_array[i] = cond;
	}
}

int get_sequence_number(){

	if(seq_number_counter < WIN_DIMENSION - 1){
		seq_number_counter++;
		return seq_number_counter;
	}else{
		seq_number_counter = -1;
		return WIN_DIMENSION - 1;
	}
}

int add_packet(PACKET pack){

	// Crea un thread per gestire il pacchetto
	pthread_t send_thread;

	return_code_send_thread = pthread_create(&send_thread, NULL, manage_packet, (void *) &pack);
	
	//PER DEBUG: così sono sicuro che i threads vengano eseguiti fino alla fine
	//pthread_join(send_thread, NULL);
	
	if (return_code_send_thread){
		printf("ERROR; return code from pthread_create() is %d\n", return_code_send_thread);
		exit(-1);
	}
}

void *manage_packet(void *packet){

	PACKET *pack = (PACKET *) packet;
	
	// CONTROLLA se c'è spazio nella finestra. Attendi se necessario


	pthread_mutex_lock(&mutex_seq_number);

	printf("Lock: mutex_seq_number\n");

	int new_seq_number = get_sequence_number();
	
	// Aggiungo numero di sequenza
	pack->seq_number = htonl(new_seq_number);

	printf("Seq number ottenuto: %u\n", new_seq_number);
	
	// Aggiungo pacchetto all'array dei pacchetti
	window[new_seq_number] = *pack;
		
	pthread_mutex_unlock(&mutex_seq_number);

	printf("Unlocked: mutex_seq_number\n");
	
	// Invia pacchetto
	invia_pacchetto(pack);
	
	/*
	// Aspetta ACK
	for(;;){
		// Lock mutex and then wait for signal to relase mutex
		pthread_mutex_lock(&(mutex_array[pack->seq_number]));

		// Wait for condition
		pthread_cond_wait(&(condition_array[pack->seq_number]), &(mutex_array[pack->seq_number]));
		
		pthread_mutex_unlock(&(mutex_array[pack->seq_number]));

	}*/


	// Aspetta timeout

	// Posso gestire queste attese in due modi: con join (creando un altro thread) oppure con condizione

	// Ricevuto ACK libera slot finestra e resetta condizione

	// Controllo se posso far avanzare la finestra

	// Termina thread
	printf("Termino thread!\n");

	pthread_exit(NULL);
}

void invia_pacchetto(PACKET *packet){
	if (sendto(packet->sock_child, (char *) packet, sizeof(*packet), 0, (struct sockaddr *) &(packet->cli_addr), sizeof(packet->cli_addr)) < 0) {
      perror("errore in sendto");
      exit(-1);
    }

	printf("Inviato pacchetto: %u\n", packet->seq_number);
}

void manage_ack(int seq_number_to_ack){

	// Invia segnale di ACK ricevuto
	/*
	pthread_mutex_lock(&(mutex_array[seq_number_to_ack]));

	pthread_cond_signal(&(condition_array[seq_number_to_ack]));

	pthread_mutex_unlock(&(mutex_array[seq_number_to_ack]));

	printf("Ricevuto ack: %u\n", seq_number_to_ack);
	*/
}

void invia_stringa(char *buff, int sock_child, struct sockaddr_in cli_addr){

	init_transmission();

	// Controlla la dimensione dei dati da inviare
	int length;
	length = strlen(buff);
	int num_pacchetti;

	if(length < MAX_PK_DATA_SIZE){// Posso inviare direttamente
		
		PACKET pack;

		// Aggiungo dati
		strcpy(pack.data, buff);

		// Inizializzo altri campi del pacchetto
		pack.sock_child = sock_child;
		pack.cli_addr = cli_addr;

		// Aggiungo il pacchetto alla coda dei pacchetti da inviare
		add_packet(pack);

	}else{// Se troppo grande, spezza in più pacchetti

		// Calcolo quanti pacchetti sono necessari
		num_pacchetti = (int) length / (MAX_PK_DATA_SIZE - 1);
		
		if(length % (MAX_PK_DATA_SIZE - 1) > 0){
			num_pacchetti++;			
		}

		// Preparo i pacchetti
		int i;
		int start;

		start = 0;

		for(i = 0; i < num_pacchetti; i++){

			PACKET pack;

			// Aggiungo dati
			strncpy(pack.data, &buff[start], MAX_PK_DATA_SIZE - 1);

			// Inizializzo altri campi del pacchetto
			pack.sock_child = sock_child;
			pack.cli_addr = cli_addr;

			int temp = start;
			
			// Incremento il contatore di posizione
			start += MAX_PK_DATA_SIZE - 1;	

			printf("Add packet: da %u a %u\n", temp, start);

			add_packet(pack);
		}
	}
}
