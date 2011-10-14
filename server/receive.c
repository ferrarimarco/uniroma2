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
pthread_t receiver_thread;
pthread_t ack_sender_thread;

// Conditions
pthread_cond_t coordinate_condition = PTHREAD_COND_INITIALIZER;
pthread_cond_t receive_condition = PTHREAD_COND_INITIALIZER;
pthread_cond_t ack_send_condition = PTHREAD_COND_INITIALIZER;

// Mutexes
pthread_mutex_t coordinate_mutex = PTHREAD_MUTEX_INITIALIZER;

// Data Mutexes
pthread_mutex_t transm_complete_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t window_mutex = PTHREAD_MUTEX_INITIALIZER;

// Thread coordination
int transmission_complete = 0;
int file_write_complete = 0;
int all_received = 0;

// Transmission management
int sock;
struct sockaddr_in cli_address;
int cli_length = sizeof(cli_address);

// Transmission window management
PACKET window[WIN_DIMENSION];
int window_base = 0;

// File write management
FILE *file;
PACKET buffer_write_file[BUFFER_FILE_DIMENSION];

// Prototypes
void *coordinator_thread_func(void *arg);
void *receiver_thread_func(void *arg);

void receive_data(char *path, int sock_child, struct sockaddr_in cli_addr){
	
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
	
	// Creo il file
	file = fopen(path, "wb");
	
	if(file == NULL){
		perror("Errore: impossibile aprire file");
		exit(1);
	}

	int i;
	
	// Inizializzo la finestra
	for(i = 0; i < WIN_DIMENSION ; i++){
		PACKET packet = {0, -1, "0", sock, cli_address};
		window[i] = packet;
	}
	
	// Creo il thread per gestire la ricezione
	if(pthread_create(&receiver_thread, NULL, receiver_thread_func, NULL) == EAGAIN){	
		perror("Errore (pthread_create): sender_thread");
		exit(1);
	}
	
	pthread_join(&receiver_thread);
	
	printf("coordinator_thread_func - Termino\n");
	
	// Termino thread
	pthread_exit(NULL);
}

void *receiver_thread_func(void *arg){

	// Per accogliere il pacchetto ricevuto
	PACKET *rcv_pack;
	
	int rec_from_res = 0;
	int transmission_complete_receiver = 0;
	int all_received_receiver = 0;
	
	while(transmission_complete_receiver != 1){

		// Controllo se devo eseguire il while di nuovo
		pthread_mutex_lock(&transm_complete_mutex);

		transmission_complete_receiver = transmission_complete;

		all_received_receiver = all_received;

		pthread_mutex_unlock(&transm_complete_mutex);
		
		
		if(!all_received_receiver){
		
			rcv_pack = malloc(sizeof(*rcv_pack));
			
			rec_from_res = recvfrom(sock, (char *) rcv_pack, sizeof(*rcv_pack), 0, (struct sockaddr *) &cli_address, &cli_length);

			if(rec_from_res == EAGAIN || rec_from_res ==  EWOULDBLOCK){
				perror("errore in recvfrom");
				exit(1);
			}else{
				if(rcv_pack->seq_number >= window_base){ // Pacchetto ricevuto è nella finestra
					
					pthread_mutex_lock(&window_mutex);

					if(window[rcv_pack->seq_number % WIN_DIMENSION].status != 3){ // Pacchetto ricevuto non ancora riscontrato
						// Copio il pacchetto nella finestra di ricezione
						window[rcv_pack->seq_number % WIN_DIMENSION].seq_number = rcv_pack->seq_number;
						window[rcv_pack->seq_number % WIN_DIMENSION].status = 2;
						strcpy(window[window_position].data, buffer_read_file[buffer_position].data);
					}
					
					// Invio ACK
					char *buff_ack;
					buff_ack = (char *) malloc(MAX_PK_DATA_SIZE);
					strcat(buff_ack, "ACK");
					sprintf(buff_ack, "%s%i", buff_ack, pack->seq_number);
					strcat(buff_ack, "\0");

					PACKET snd_pack;
					snd_pack.seq_number = rcv_pack->seq_number;
					snd_pack.status = 2;
					strcpy(snd_pack.data, buff_ack);
					snd_pack.sock_child = sock;
					snd_pack.cli_addr = cli_address;
					
					if (sendto(sock, (char *) &snd_pack, sizeof(snd_pack), 0, (struct sockaddr *) &cli_address, &cli_length) < 0) {
						perror("errore in sendto");
						exit(1);
					}
					
					// Imposto stato del pacchetto a 3 (ricevuto e riscontrato)
					window[rcv_pack->seq_number % WIN_DIMENSION].status = 3;

					if(window[window_base % WIN_DIMENSION].status == 3){ // Faccio avanzare la finestra se necessario
					
						while(window[window_base % WIN_DIMENSION].status == 3){
							
							// Scrivo su file
							scrivi_file(file, &(window[window_base % WIN_DIMENSION].data));
							
							window[window_base % WIN_DIMENSION].status = -1;
							window_base++;
						}
						
						// Invio segnale a coordinator_thread (available_window_space è stato modificato)
						pthread_mutex_lock(&coordinate_mutex);
						pthread_cond_signal(&coordinate_condition);
						pthread_mutex_unlock(&coordinate_mutex);
					}
					
					pthread_mutex_unlock(&window_mutex);
				}
			}
		}
	}
	
	// Chiudo file se ho finito di scrivere
	fclose(file);
	
	printf("receiver_thread_func - Termino\n");
	
	// Termino thread
	pthread_exit(NULL);
}