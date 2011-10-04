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

// Transmission management
int seq_number_counter;

// Thread management
pthread_mutex_t mutex_array[WIN_DIMENSION];
pthread_cond_t  condition_array[WIN_DIMENSION];
pthread_t thread_array[WIN_DIMENSION];
pthread_t waker_thread_array[WIN_DIMENSION];

// Protoypes
void *manage_packet_thread_func(void *packet);

void init_transmission(){
	int i;
	seq_number_counter = 0;

	for(i = 0; i < WIN_DIMENSION; i++){
		
		pthread_mutex_t mut = PTHREAD_MUTEX_INITIALIZER;
		pthread_cond_t cond = PTHREAD_COND_INITIALIZER;

		mutex_array[i] = mut;
		condition_array[i] = cond;
	}
}

void sender_window(char *command, int sock_child, struct sockaddr_in cli_addr){
	
	// Imposto socket non bloccanti
	fcntl(sock_child, F_SETFL, O_NONBLOCK);
	
	// Per gestire finestra
	PACKET window[WIN_DIMENSION];
	int i;
	
	// Per gestire trasmissione
	int transmission_complete = 0;
	int window_base = 0;
	int packet_number = 0;
	int next_sequence_number = 0;
	int needed_packets = file_size(command);
	int cli_length = sizeof(cli_addr);
	
	// Per gestire lettura da file
	FILE *file;
	int nread = 0;
	unsigned char *buffer_read_file;
	unsigned char *temp;
	
	printf("SOCK CHILD: %u\n", sock_child);
	printf("COMMAND: %s\n", command);
	
	//Inizializzo la finestra
	for(i=0; i < WIN_DIMENSION ; i++){
		PACKET packet = {0, 0, "0", sock_child, cli_addr};
		window[i] = packet;
	}
	
	errno = 0;
	
	// Inizio lettura da file
	file = fopen(command, "rb");

	printf("fopen_errno: %s\n", strerror(errno));
	
	if(file == NULL){
		puts("cannot open file.");
		exit(1);
	}

	while(transmission_complete == 0){

		while(window[packet_number % WIN_DIMENSION].status == 0 && transmission_complete == 0){ // Riempio la finestra e invio
			
			
			if(leggi_file_position(file, &buffer_read_file)){
				
				// Assegno numero di seq al pacchetto
				window[packet_number % WIN_DIMENSION].seq_number = packet_number;
				
				printf("PRE window[packet_number % WIN_DIMENSION].sock_child: %i\n", window[packet_number % WIN_DIMENSION].sock_child);
				
				// Inserisco dati nel pacchetto
				strcpy(window[packet_number % WIN_DIMENSION].data, buffer_read_file);

				printf("POST window[packet_number % WIN_DIMENSION].sock_child: %i\n", window[packet_number % WIN_DIMENSION].sock_child);
				
				// Sleep prima di inviare				
				//usleep(500);
				
				// Invio pacchetto
				errno = 0;
				int send_ret = sendto(window[packet_number % WIN_DIMENSION].sock_child, (char *) &(window[packet_number % WIN_DIMENSION]), sizeof(window[packet_number % WIN_DIMENSION]), 0, (struct sockaddr *) &((&(window[packet_number % WIN_DIMENSION]))->cli_addr), cli_length);
				printf("sendto err: %s\n", strerror(errno));
				
				if(send_ret  == EAGAIN || send_ret ==  EWOULDBLOCK){
					perror("Errore in sendto");
					exit(1);
				}else{
					// Debug
					printf("Pacchetto %u inviato\n", window[packet_number % WIN_DIMENSION].seq_number);
					
					// Imposto stato del pacchetto (1 = inviato ma non ancora riscontrato)
					window[packet_number % WIN_DIMENSION].status = 1;
					
					// Creo thread per gestire eventuali ritrasmissioni
					
					// Argomento della funzione del thread
					void* packet_pointer = &window[packet_number % WIN_DIMENSION];
					/*
					if(pthread_create(&thread_array[packet_number % WIN_DIMENSION], NULL, manage_packet_thread_func, packet_pointer) == EAGAIN){	
						perror("errore in pthread_create");
						exit(1);
					}*/
					
					// Per far proseguire la spedizione
					packet_number++;
					
					// Per assegnare il prossimo numero di sequenza
					next_sequence_number++;
					
				}
		
			}else{ // Ho finito di leggere il file da inviare
				break;
			}
		}
		/*
		while(window[window_base % WIN_DIMENSION].status != 2){ // Aspetto gli ACK
			
			if(window_base >= (needed_packets - 1)){ // Trasmissione completata
				transmission_complete = 1;
				break;
			}
			
			// Per accogliere il pacchetto ricevuto
			PACKET *rcv_pack;
			rcv_pack = malloc(sizeof(*rcv_pack));
			
			int rec_from_res = 0;
			int cli_length = sizeof(cli_addr);
			
			rec_from_res = recvfrom(sock_child, (char *) rcv_pack, sizeof(*rcv_pack), 0, (struct sockaddr *) &cli_addr, &cli_length);
				
			if(rec_from_res == EAGAIN || rec_from_res ==  EWOULDBLOCK) {
				perror("errore in recvfrom");
				exit(1);
			}else{
				
				if(rcv_pack->seq_number >= window_base){ // Pacchetto ricevuto è nella finestra
					
					if(window[rcv_pack->seq_number % WIN_DIMENSION].status != 2){ // Pacchetto ricevuto non ancora riscontrato
					
						// Imposto status a 2 (pacchetto inviato e riscontrato
						window[rcv_pack->seq_number % WIN_DIMENSION].status = 2;
						
						void* packet_pointer = &window[rcv_pack->seq_number % WIN_DIMENSION];
						
						/*
						if (pthread_create(&waker_thread_array[rcv_pack->seq_number % WIN_DIMENSION], NULL, Observer, p) == EAGAIN){
							perror("errore in pthread_create ");
							exit(1);
						}
						
						pthread_join(thread_array[rcv_pack->seq_number % WIN_DIMENSION], NULL);
						pthread_join(waker_thread_array[rcv_pack->seq_number % WIN_DIMENSION], NULL);
						*//*
					}

					if(window[window_base % WIN_DIMENSION].status == 2){ // Faccio avanzare la finestra
						
						while(window[window_base % WIN_DIMENSION].status != 2){
							window[window_base % WIN_DIMENSION].status = 0;
							window_base++;
						} 
				
						break;
					}
		   
				}
			}
		
			if(window_base >= (needed_packets - 1)){ // Trasmissione completata
				transmission_complete = 1;
				break;
			}		
		}*/
	}
	
	// Chiudo file
	fclose(file);
}

void *manage_packet_thread_func(void *packet){
	PACKET *p = (PACKET *) packet;
}