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

#include <time.h>

#include <pthread.h>

#include "Packet.h"
#include "constants.h"

// Threads
pthread_t receiver_thread;

// Thread coordination
int all_received = 0;

// Transmission management
int sock;
struct sockaddr_in cli_address;
int cli_length = sizeof(cli_address);
int packets_to_check = WIN_DIMENSION;

// Timeout per gestire la chiusura della connessione
time_t timeout;

// Transmission window management
PACKET window[WIN_DIMENSION];
int window_base = 0;

// File write management
FILE *file;
PACKET buffer_write_file[BUFFER_FILE_DIMENSION];

// Prototypes
void *receiver_thread_func(void *arg);

void receive_data(char *path, int sock_child, struct sockaddr_in cli_addr){

	// Creo il file
	file = fopen(path, "wb");

	if(file == NULL){
		perror("Errore: impossibile aprire file");
		exit(1);
	}

	int i;

	// Inizializzo la finestra
	for(i = 0; i < WIN_DIMENSION ; i++){
		PACKET packet = {-1, -1, MAX_PK_DATA_SIZE, WIN_DIMENSION, "0", sock, cli_address};
		window[i] = packet;
	}

	// Creo il thread per gestire la ricezione
	if(pthread_create(&receiver_thread, NULL, receiver_thread_func, NULL) == EAGAIN){
		perror("Errore (pthread_create): sender_thread");
		exit(1);
	}

	// Inizializzazione
	sock = sock_child;
	cli_address = cli_addr;

	// Inizializzo seme per rand
	srand(time(0));
	
	// Join su thread coordintatore
	pthread_join(receiver_thread, NULL);

}

void *receiver_thread_func(void *arg){

	// Per accogliere il pacchetto ricevuto
	PACKET *rcv_pack;

	int rec_from_res = 0;

	int received_seq_number = -1;
	float rand_for_loss = 0.0;

	double diff_t = 0.0;
	
	int test_how_many_pk = 0;
	
	while(!all_received || packets_to_check > 0){

		rcv_pack = malloc(sizeof(*rcv_pack));

		// Inizializzo rcv_pack
		rcv_pack->seq_number = -1;
		rcv_pack->status = -1;
		rcv_pack->data_size = MAX_PK_DATA_SIZE;
		rcv_pack->packets_to_check = WIN_DIMENSION;
		rcv_pack->sock_child = sock;
		rcv_pack->cli_addr = cli_address;
		
		rec_from_res = recvfrom(sock, (char *) rcv_pack, sizeof(*rcv_pack), 0, (struct sockaddr *) &cli_address, &cli_length);

		if(rec_from_res == EAGAIN || rec_from_res ==  EWOULDBLOCK){
			perror("errore in recvfrom");
			exit(1);
		}else{
		
			rand_for_loss = (float) (rand() % 10) / 10;
			
			if(rand_for_loss <= (1 - LOSS_PROBABILITY)){
				
				if(rcv_pack->seq_number != -1)
					received_seq_number = ntohl(rcv_pack->seq_number);

				if(received_seq_number >= window_base){ // Pacchetto ricevuto in finestra
					
					//printf("PK %i è nella finestra\n", received_seq_number);
					
					if(window[received_seq_number % WIN_DIMENSION].status != 3){ // Pacchetto ricevuto non ancora riscontrato
					
						//printf("Riscontro PK %i\n", received_seq_number);

						// Copio il pacchetto nella finestra di ricezione
						window[received_seq_number % WIN_DIMENSION].seq_number = received_seq_number;
						window[received_seq_number % WIN_DIMENSION].data_size = ntohl(rcv_pack->data_size);
						window[received_seq_number % WIN_DIMENSION].status = 3;
						window[received_seq_number % WIN_DIMENSION].last_one = ntohl(rcv_pack->last_one);
						
						memcpy(&(window[received_seq_number % WIN_DIMENSION].data), rcv_pack->data, window[received_seq_number % WIN_DIMENSION].data_size * sizeof(char));
					}

					//printf("Base - window_base: %i - seq_number: %i, status: %i\n", window_base, window[window_base % WIN_DIMENSION].seq_number, window[window_base % WIN_DIMENSION].status);
					
					if(window[window_base % WIN_DIMENSION].status == 3){ // Faccio avanzare la finestra se necessario
					
						while(window[window_base % WIN_DIMENSION].status == 3){

							// Scrivo su file
							scrivi_file(file, window[window_base % WIN_DIMENSION].data, window[window_base % WIN_DIMENSION].data_size);
							
							test_how_many_pk++;
							printf("Ho scritto dati del pk %i, Totale pk scritti: %i\n", window[window_base % WIN_DIMENSION].seq_number, test_how_many_pk);
							
							window[window_base % WIN_DIMENSION].status = -1;
							
							// Controllo se ho terminato la trasmissione
							if(window[window_base % WIN_DIMENSION].last_one){// Ho terminato la trasmissione
								printf("\nTutti ricevuti, imposto socket a non bloccante\n");
								all_received = 1;
								
								// Chiudo file se ho finito di scrivere
								fclose(file);
								
								// Imposto timeout per gestire la chiusura della connessione
								timeout = time(NULL);
								
								// Imposto socket non bloccante
								fcntl(sock, F_SETFL, O_NONBLOCK);
							
							}else{
								window_base++;
							}
						}
					}
				}
				
				packets_to_check = ntohl(rcv_pack->packets_to_check);
				
				// Invio ACK
				char *buff_ack;
				buff_ack = (char *) malloc(MAX_PK_DATA_SIZE);
				strcat(buff_ack, "ACK");
				sprintf(buff_ack, "%s%i", buff_ack, received_seq_number);
				strcat(buff_ack, "\0");

				PACKET snd_pack;
				snd_pack.seq_number = htonl(received_seq_number);
				snd_pack.status = 2;
				strcpy(snd_pack.data, buff_ack);
				snd_pack.sock_child = sock;
				snd_pack.cli_addr = cli_address;

				
				if(snd_pack.seq_number != -1){
					//printf("Invio ACK - seq_number: %i, data: %s\n", snd_pack.seq_number, snd_pack.data);
					if (sendto(sock, (char *) &snd_pack, sizeof(snd_pack), 0, (struct sockaddr *) &cli_address, cli_length) < 0) {
						perror("errore in sendto");
						exit(1);
					}
				}
				
			}else{// Scarto pacchetto per loss prob
				if(rcv_pack->seq_number != -1)
					printf("Scarto PK%i per loss prob. rand_for_loss: %f\n", received_seq_number, rand_for_loss);				
			}
		}
		
		
		
		// Se non arriva nulla, packets to check = 0		
		diff_t = difftime(time(NULL), timeout);
		
		// Conversione in msec
		diff_t = diff_t * 1000;

		// Aspetto per un tempo pari al doppio del timeout
		if(diff_t >= 3 * TIMEOUT){// Tempo scaduto
			
			// Per uscire dal while
			packets_to_check = 0;
		}
	}

	printf("receiver_thread_func - Termino\n");

	// Termino thread
	pthread_exit(NULL);
}
