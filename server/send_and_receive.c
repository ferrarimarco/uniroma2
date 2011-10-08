#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <errno.h>

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <fcntl.h>

#include <pthread.h>

#include "Packet.h"
#include "constants.h"


// Threads
pthread_t coordinator_thread;
pthread_t sender_thread;
pthread_t file_reader_thread;
pthread_t ack_checker_thread;
pthread_t timeout_watcher_thread;

// Conditions
pthread_cond_t coordinate_condition = PTHREAD_COND_INITIALIZER;
pthread_cond_t send_condition = PTHREAD_COND_INITIALIZER;
pthread_cond_t file_read_condition = PTHREAD_COND_INITIALIZER;
pthread_cond_t ack_check_condition = PTHREAD_COND_INITIALIZER;
pthread_cond_t timeout_watch_condition = PTHREAD_COND_INITIALIZER;

// Mutexes
pthread_mutex_t coordinate_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t timeout_watch_mutex = PTHREAD_MUTEX_INITIALIZER;

// Data Mutexes
pthread_mutex_t transm_complete_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t buffer_read_file_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t available_window_space_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t available_buffer_space_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t window_mutex = PTHREAD_MUTEX_INITIALIZER;

// Thread coordination
int transmission_complete = 0;
int file_read_complete = 0;
int all_sent = 0;
int all_acked = 0;
int available_buffer_space = BUFFER_READ_FILE_DIMENSION;
int available_window_space = WIN_DIMENSION;

// Transmission management
int sock;
struct sockaddr_in cli_address;
int cli_length = sizeof(cli_address);

// Transmission window management
int needed_packets;
PACKET window[WIN_DIMENSION];
int window_base = 0;
int next_sequence_number = 0;


// File read management
FILE *file;
int nread = 0;
unsigned char *temp_read_file;
PACKET buffer_read_file[BUFFER_READ_FILE_DIMENSION];

// Prototypes
void *coordinator_thread_func(void *arg);
void *sender_thread_func(void *arg);
void *file_reader_thread_func(void *arg);
void *ack_checker_thread_func(void *arg);
void *timeout_watcher_thread_func(void *arg);


void send_data(char *path, int sock_child, struct sockaddr_in cli_addr){
	
	// Inizializzazione
	sock = sock_child;
	cli_address = cli_addr;
	
	// Thread coordinatore
	void *path_pointer = (void *) path;
	
	if(pthread_create(&coordinator_thread, NULL, coordinator_thread_func, path_pointer) == EAGAIN){	
		perror("Errore (pthread_create): coordinator_thread");
		exit(1);
	}	

	// Join su thread coordintatore
	pthread_join(coordinator_thread, NULL);
}

void *coordinator_thread_func(void *arg){
	
	char *path = (char *) arg;
	
	// Conto i pacchetti necessari all'invio
	pthread_mutex_lock(&transm_complete_mutex);
	
	needed_packets = file_size(path);
	
	pthread_mutex_unlock(&transm_complete_mutex);
	

	printf("coord_func - Path: %s\n", path);
	printf("coord_func - needed packets: %u\n", needed_packets);

	int i;
	
	// Inizializzo la finestra
	for(i = 0; i < WIN_DIMENSION ; i++){
		PACKET packet = {0, -1, "0", sock, cli_address};
		window[i] = packet;
	}
	
	// Inizializzo il buffer di lettura
	for(i = 0; i < BUFFER_READ_FILE_DIMENSION ; i++){
		PACKET packet = {0, -1, "0", sock, cli_address};
		buffer_read_file[i] = packet;
	}
	
	// Creo i thread per gestire la trasmissione
	if(pthread_create(&ack_checker_thread, NULL, ack_checker_thread_func, NULL) == EAGAIN){	
		perror("Errore (pthread_create): ack_checker_thread");
		exit(1);
	}
	
	usleep(50);

	if(pthread_create(&sender_thread, NULL, sender_thread_func, NULL) == EAGAIN){	
		perror("Errore (pthread_create): sender_thread");
		exit(1);
	}
		
	if(pthread_create(&file_reader_thread, NULL, file_reader_thread_func, (void *) path) == EAGAIN){	
		perror("Errore (pthread_create): file_reader_thread");
		exit(1);
	}
	
	if(pthread_create(&timeout_watcher_thread, NULL, timeout_watcher_thread_func, NULL) == EAGAIN){	
		perror("Errore (pthread_create): timeout_watcher_thread");
		exit(1);
	}
	
	int transm_complete_coord = 0;
	
	// Aspetto che gli altri threads siano pronti
	usleep(500);
	
	while(transm_complete_coord != 1){
		
		// Controllo se devo eseguire il while di nuovo
		pthread_mutex_lock(&transm_complete_mutex);
				
		transm_complete_coord = transmission_complete;
		
		printf("coord_func - frc: %i, aa: %i\n", file_read_complete, all_acked);
		
		// Controllo se ho completato la trasmissione
		if(file_read_complete && all_acked){
			transmission_complete = 1;
			printf("coord_func - trasmissione completata\n");	
		}
		
		pthread_mutex_unlock(&transm_complete_mutex);
				
		// Controllo se devo svegliare il thread che riempie il buffer
		pthread_mutex_lock(&available_buffer_space_mutex);
		//printf("coord_func - file_reader_mutex acquisito\n");
		
		if(available_buffer_space >= 0){
			//printf("coord_func - segnalo al file reader\n");
			pthread_cond_signal(&file_read_condition);
		}
		
		//printf("coord_func - rilascio file_read_mutex\n");
		pthread_mutex_unlock(&available_buffer_space_mutex);
		
		// Controllo se devo svegliare il thread che invia i pacchetti
		pthread_mutex_lock(&available_window_space_mutex);
		
		if(available_window_space > 0){			
			pthread_cond_signal(&send_condition);
		}
		
		pthread_mutex_unlock(&available_window_space_mutex);
		
		// Controllo se devo svegliare il thread che invia i pacchetti
		pthread_mutex_lock(&available_buffer_space_mutex);
		
		if(BUFFER_READ_FILE_DIMENSION - available_buffer_space > 0){
			pthread_cond_signal(&send_condition);
		}
		
		pthread_mutex_unlock(&available_buffer_space_mutex);
		
		// Attendo sulla condizione coordinate_condition, in attesa che cambino uno tra:
		// Controllo fine trasmissione: file_read_complete, all_acked
		// Controllo prossima operazione: available_buffer_space, available_window_space
		
		pthread_mutex_lock(&transm_complete_mutex);
				
		transm_complete_coord = transmission_complete;
		
		pthread_mutex_unlock(&transm_complete_mutex);
		
		if(!transm_complete_coord){
			pthread_mutex_lock(&coordinate_mutex);

			pthread_cond_wait(&coordinate_condition, &coordinate_mutex);

			pthread_mutex_unlock(&coordinate_mutex);
		}
	}
	
	pthread_cond_signal(&send_condition);
	
	printf("Attendo sender_thread\n");
	pthread_join(sender_thread, NULL);
	printf("Terminato sender_thread\n");
	
	printf("Attendo file_reader_thread\n");
	pthread_join(file_reader_thread, NULL);
	printf("Terminato file_reader_thread\n");
	
	printf("Attendo ack_checker_thread\n");
	pthread_join(ack_checker_thread, NULL);
	printf("Terminato ack_checker_thread\n");
	
	printf("Attendo timeout_watcher_thread\n");
	pthread_join(timeout_watcher_thread, NULL);
	printf("Terminato timeout_watcher_thread\n");
	
	printf("coordinator_thread_func - Termino\n");
	
	
	// Termino thread
	pthread_exit(NULL);

}

void *sender_thread_func(void *arg){
	
	int transm_complete_sender = 0;
	int all_acked_sender = 0;
	int buffer_position;
	int window_position;
	
	while(!transm_complete_sender){
		buffer_position = next_sequence_number % BUFFER_READ_FILE_DIMENSION;
		window_position = next_sequence_number % WIN_DIMENSION;		
		
		// Controllo se devo eseguire il while di nuovo
		pthread_mutex_lock(&transm_complete_mutex);
		
		transm_complete_sender = transmission_complete;
		
		// Controllo se ho completato la trasmissione
		if(transm_complete_sender){// Trasmissione completata: sblocco il mutex ed esco dal while
		      pthread_mutex_unlock(&transm_complete_mutex);
		      break;
		}
		
		pthread_mutex_unlock(&transm_complete_mutex);
		
		pthread_mutex_lock(&transm_complete_mutex);

		all_acked_sender = all_acked;

		pthread_mutex_unlock(&transm_complete_mutex);	
		
		if(!all_acked_sender){
		
			// Aspetto se finestra è piena
			pthread_mutex_lock(&available_window_space_mutex);
			
			while(available_window_space == 0){
				printf("sender_thread_func - dormo: window full\n");	
				pthread_cond_wait(&send_condition, &available_window_space_mutex);
			}

			pthread_mutex_unlock(&available_window_space_mutex);
			
			pthread_mutex_lock(&transm_complete_mutex);
			
			// Controllo se ho completato la trasmissione
			if(transm_complete_sender){
			pthread_mutex_unlock(&transm_complete_mutex);
			break;
			}
			
			pthread_mutex_unlock(&transm_complete_mutex);
			
			// Aspetto se buffer vuoto
			pthread_mutex_lock(&available_buffer_space_mutex);

			while(BUFFER_READ_FILE_DIMENSION - available_buffer_space == 0){
				printf("sender_thread_func - dormo: buffer vuoto\n");
				pthread_cond_wait(&send_condition, &available_buffer_space_mutex);
			}
			
			pthread_mutex_unlock(&available_buffer_space_mutex);
			
			pthread_mutex_lock(&transm_complete_mutex);
			
			// Controllo se ho completato la trasmissione
			if(transm_complete_sender){
			pthread_mutex_unlock(&transm_complete_mutex);
			break;
			}
			
			pthread_mutex_unlock(&transm_complete_mutex);
			
			// Prendo dati da buffer
			
			pthread_mutex_lock(&window_mutex);
			
			// Imposto stato del pacchetto (1 = non trasmesso ma in finestra)
			window[window_position].status = 1;
			window[window_position].seq_number = next_sequence_number;
			//window[window_position].seq_number = htonl(next_sequence_number);
			
			pthread_mutex_lock(&buffer_read_file_mutex);

			strcpy(window[window_position].data, buffer_read_file[buffer_position].data);
			//memcpy(window[window_position].data, buffer_read_file[buffer_position].data, sizeof(buffer_read_file[buffer_position].data));
			
			// Imposto stato del pacchetto (1 = non trasmesso ma in finestra)
			buffer_read_file[buffer_position].status = 1;
			
			pthread_mutex_unlock(&buffer_read_file_mutex);
			
			// Invio pacchetto
			int send_ret = sendto(window[window_position].sock_child, (char *) &(window[window_position]), sizeof(window[window_position]), 0, (struct sockaddr *) &((&(window[window_position]))->cli_addr), cli_length);
			
			
			if(send_ret  == EAGAIN || send_ret ==  EWOULDBLOCK){
				perror("sender_thread_func - Errore in sendto");
				exit(1);
			}else{
				
				printf("sender_thread_func - Pacchetto %u inviato\n", window[window_position].seq_number);
			
				// Imposto stato del pacchetto (2 = inviato ma non ancora riscontrato)
				window[window_position].status = 2;
				
				pthread_mutex_unlock(&window_mutex);
				
				// Per assegnare il prossimo numero di sequenza
				next_sequence_number++;
				
				// Decremento packets_to_send di uno
				pthread_mutex_lock(&available_window_space_mutex);
				
				available_window_space--;

				pthread_mutex_unlock(&available_window_space_mutex);
				
				// Invio segnale a coordinator_thread (available_window_space è stato modificato)
				pthread_mutex_lock(&coordinate_mutex);
				
				pthread_cond_signal(&coordinate_condition);
				
				pthread_mutex_unlock(&coordinate_mutex);		
				
				// Incremento available_buffer_space di uno
				pthread_mutex_lock(&available_buffer_space_mutex);
				
				available_buffer_space++;
				
				pthread_mutex_unlock(&available_buffer_space_mutex);

				// Invio segnale a coordinator_thread (available_buffer_space è stato modificato)
				pthread_mutex_lock(&coordinate_mutex);

				pthread_cond_signal(&coordinate_condition);
				
				pthread_mutex_unlock(&coordinate_mutex);			
			}
		}
	}
	
	printf("sender_thread_func - Termino\n");
	
	// Termino thread
	pthread_exit(NULL);

}

void *file_reader_thread_func(void *arg){

	int transm_complete_file_reader = 0;
	int buffer_position_helper = 0;
	int buffer_position;
	int file_read_complete_reader = 0;
	
	char *path = (char *) arg;
	
	// Inizio lettura da file
	file = fopen(path, "rb");
	
	if(file == NULL){
		perror("Errore: impossibile aprire file");
		exit(1);
	}
		
	while(transm_complete_file_reader != 1){
		
		buffer_position = buffer_position_helper % BUFFER_READ_FILE_DIMENSION;
		
		// Controllo se devo eseguire il while di nuovo
		pthread_mutex_lock(&transm_complete_mutex);
		
		transm_complete_file_reader = transmission_complete;
		
		// Controllo se ho completato la trasmissione
		if(transm_complete_file_reader){
		      pthread_mutex_unlock(&transm_complete_mutex);
		      break;
		}
		
		pthread_mutex_unlock(&transm_complete_mutex);
		
		// Aspetto se non c'è spazio nel buffer
		pthread_mutex_lock(&available_buffer_space_mutex);
		
		while(available_buffer_space == 0 && !file_read_complete_reader){
			printf("file_reader_thread_func - dormo, buffer pieno\n");
			pthread_cond_wait(&file_read_condition, &available_buffer_space_mutex);
		}
		
		pthread_mutex_unlock(&available_buffer_space_mutex);
		
		pthread_mutex_lock(&transm_complete_mutex);
		
		// Controllo se ho completato la trasmissione
		if(transm_complete_file_reader){
		      pthread_mutex_unlock(&transm_complete_mutex);
		      break;
		}

		file_read_complete_reader = file_read_complete;

		pthread_mutex_unlock(&transm_complete_mutex);	
		
		if(!file_read_complete_reader){

			if(leggi_file(file, &temp_read_file)){// Ho letto dati	
				
				// Inserisco i dati nel pacchetto
				pthread_mutex_lock(&buffer_read_file_mutex);	
				strcpy(buffer_read_file[buffer_position].data, temp_read_file);

				// Imposto stato del pacchetto (0 = non nella finestra ma solo nel buffer)
				buffer_read_file[buffer_position].status = 0;

				pthread_mutex_unlock(&buffer_read_file_mutex);
				
				// Aggiorno buffer_position_helper
				buffer_position_helper++;
				
				// Decremento available_buffer_space di uno
				pthread_mutex_lock(&available_buffer_space_mutex);

				available_buffer_space--;

				pthread_mutex_unlock(&available_buffer_space_mutex);

				// Invio segnale a coordinator_thread (available_buffer_space è stato modificato)
				pthread_mutex_lock(&coordinate_mutex);

				pthread_cond_signal(&coordinate_condition);
				
				pthread_mutex_unlock(&coordinate_mutex);
				
			}else{// Ho finito di leggere il file da inviare
				pthread_mutex_lock(&transm_complete_mutex);

				file_read_complete = 1;
				
				printf("file_reader_thread_func - read complete!\n");
				
				pthread_mutex_unlock(&transm_complete_mutex);
				
				// Invio segnale a coordinator_thread (file_read_complete è stato modificato)
				pthread_mutex_lock(&coordinate_mutex);

				pthread_cond_signal(&coordinate_condition);
				
				pthread_mutex_unlock(&coordinate_mutex);	
			}
			
			
		}		
	}
	
	// Chiudo file se ho finito di leggere
	fclose(file);
	
	printf("file_reader_thread_func - ho finito di leggere, termino!");
	
	// Termino thread
	pthread_exit(NULL);	
}

void *ack_checker_thread_func(void *arg){
  
	// Per accogliere il pacchetto ricevuto
	PACKET *rcv_pack;
	
	int transmission_complete_ack_checker = 0;
	int all_acked_ack_checker = 0;
	
	// != 1
	while(transmission_complete_ack_checker != 1){

		// Controllo se devo eseguire il while di nuovo
		pthread_mutex_lock(&transm_complete_mutex);

		transmission_complete_ack_checker = transmission_complete;

		all_acked_ack_checker = all_acked;

		pthread_mutex_unlock(&transm_complete_mutex);			
		
		
		if(!all_acked_ack_checker){
		
			rcv_pack = malloc(sizeof(*rcv_pack));
			int rec_from_res = 0;
			rec_from_res = recvfrom(sock, (char *) rcv_pack, sizeof(*rcv_pack), 0, (struct sockaddr *) &cli_address, &cli_length);
			
			printf("ack_checker_thread_func - Ricevuto ACK%i\n", rcv_pack->seq_number);
			
			if(rec_from_res == EAGAIN || rec_from_res ==  EWOULDBLOCK) {
				perror("errore in recvfrom");
				exit(1);
			}else{
				if(rcv_pack->seq_number >= window_base){ // Pacchetto ricevuto è nella finestra
					
					pthread_mutex_lock(&window_mutex);

					if(window[rcv_pack->seq_number % WIN_DIMENSION].status != 3){ // Pacchetto ricevuto non ancora riscontrato
				
						// Imposto status a 3 (3 = pacchetto inviato e riscontrato)
						window[rcv_pack->seq_number % WIN_DIMENSION].status = 3;						
					}

					if(window[window_base % WIN_DIMENSION].status == 3){ // Faccio avanzare la finestra
					
						while(window[window_base % WIN_DIMENSION].status == 3){
							
							window[window_base % WIN_DIMENSION].status = -1;
							window_base++;
							
							pthread_mutex_lock(&available_window_space_mutex);
							available_window_space++;						
							pthread_mutex_unlock(&available_window_space_mutex);
						}
						
						// Invio segnale a coordinator_thread (available_window_space è stato modificato)
						pthread_mutex_lock(&coordinate_mutex);
						pthread_cond_signal(&coordinate_condition);
						pthread_mutex_unlock(&coordinate_mutex);
					}
					
					pthread_mutex_unlock(&window_mutex);
		  
				}
			}

			pthread_mutex_lock(&transm_complete_mutex);
			
			if(window_base >= needed_packets){ // Trasmissione completata
				
				all_acked = 1;
				
				pthread_mutex_unlock(&transm_complete_mutex);
				
				printf("ack_checker_thread_func - All acked\n");
				
				// Invio segnale a coordinator_thread (all_acked è stato modificato)
				pthread_mutex_lock(&coordinate_mutex);
				pthread_cond_signal(&coordinate_condition);
				pthread_mutex_unlock(&coordinate_mutex);				
				
				break;
			}
			
			pthread_mutex_unlock(&transm_complete_mutex);
		
		}

	}
	
	printf("ack_checker_thread_func - Termino\n");
	
	// Termino thread
	pthread_exit(NULL);
}

void *timeout_watcher_thread_func(void *arg){

}
