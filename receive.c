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

#include "constants.h"

// Threads
pthread_t receiver_thread;

// Transmission management
int sock;
struct sockaddr_in *cli_address;
int cli_length_receive = sizeof(*cli_address);
int command_mode_receiver = 0;

// Timeout per gestire la chiusura della connessione
time_t timeout;

// Transmission window management
PACKET window_receive[WIN_DIMENSION];

// File write management
FILE *file;
PACKET buffer_write_file[BUFFER_FILE_DIMENSION];
char *path_receiver;

// File log receiver
FILE *logfile_rec;

// Prototypes
void *receiver_thread_func(void *arg);

void receive_data(char *path, int sock_child, struct sockaddr_in *cli_addr, int is_command){

	if(LOG_TO_TEXT_FILE)
		logfile_rec = fopen(RECEIVER_LOG_FILE_PATH, "w");
	
	// Creo il file
	if(!is_command){
		if(path != NULL)
			file = fopen(path, "wb");
		else
			file = stdout;
	}
	
	path_receiver = path;
	
	command_mode_receiver = is_command;

	if(file == NULL && !is_command){
		perror("Errore: impossibile aprire file");
		exit(1);
	}

	int i;
	
	// Inizializzazione
	sock = sock_child;
	cli_address = cli_addr;
	
	// Inizializzo la finestra
	for(i = 0; i < WIN_DIMENSION ; i++){
		PACKET packet = {-1, -1, MAX_PK_DATA_SIZE, WIN_DIMENSION, "0", sock, *cli_address, 0, 0, 0};
		window_receive[i] = packet;
	}

	// Inizializzo seme per rand
	srand(time(0));
	
	printf("receive_data: inizializzazione completata\n");
	
	if(LOG_TO_TEXT_FILE)
		fprintf(logfile_rec, "receive_data: inizializzazione completata\n");
	
	// Creo il thread per gestire la ricezione
	if(pthread_create(&receiver_thread, NULL, receiver_thread_func, NULL) == EAGAIN){
		if(LOG_TO_TEXT_FILE)
			fprintf(logfile_rec, "Errore (pthread_create): sender_thread");
		
		perror("Errore (pthread_create): sender_thread");
		exit(1);
	}
	
	// Join su thread coordintatore
	pthread_join(receiver_thread, NULL);

}

void *receiver_thread_func(void *arg){

	// Per accogliere il pacchetto ricevuto
	PACKET *rcv_pack;

	int rec_from_res = 0;
	int all_received = 0;
	int received_seq_number = -1;
	float rand_for_loss = 0.0;
	int timeout_started = 0;
	double diff_t = 0.0;
	int window_base = 0;
	int packets_to_check_receive = WIN_DIMENSION;
	
	int socket_options = 0;
	socket_options = fcntl(sock,F_GETFL);
	socket_options = socket_options & (~O_NONBLOCK);

	while(!all_received && packets_to_check_receive > 0){

		rcv_pack = malloc(sizeof(*rcv_pack));

		// Inizializzo rcv_pack
		rcv_pack->seq_number = -1;
		rcv_pack->status = -1;
		rcv_pack->data_size = MAX_PK_DATA_SIZE;
		rcv_pack->packets_to_check = WIN_DIMENSION;
		rcv_pack->sock_child = sock;
		rcv_pack->cli_addr = *cli_address;
		rcv_pack->last_one = 0;
		rcv_pack->error_packet = 0;
		rcv_pack->command_packet = 0;
		
		printf("mi blocco sulla rcvfrom\n");
		
		rec_from_res = recvfrom(sock, (char *) rcv_pack, sizeof(*rcv_pack), 0, (struct sockaddr *) cli_address, &cli_length_receive );
		
		printf("ho ricevuto qualcosa\n");
		
		printf("send_data - Indirizzo mittente udp://%s:%u\n", inet_ntoa(cli_address->sin_addr), ntohs(cli_address->sin_port));
		
		if(rec_from_res == EAGAIN || rec_from_res ==  EWOULDBLOCK){
			perror("errore in recvfrom");
			exit(1);
		}else{
			
			if(rcv_pack->seq_number != -1){
				received_seq_number = ntohl(rcv_pack->seq_number);
			}else{
				received_seq_number = -1;
				printf("scarto pk, seq_number è -1\n");
			}
		
			rand_for_loss = (float) (rand() % 10) / 10;
			
			printf("rand_for_loss: %f\n");
			
			if(rand_for_loss <= (1 - LOSS_PROBABILITY) && received_seq_number != -1){

				if(received_seq_number >= window_base){ // Pacchetto ricevuto in finestra
					
					printf("ricevuto pacchetto: %i\n", received_seq_number);
					
					if(window_receive[received_seq_number % WIN_DIMENSION].status != 3){ // Pacchetto ricevuto non ancora riscontrato

						// Copio il pacchetto nella finestra di ricezione
						window_receive[received_seq_number % WIN_DIMENSION].seq_number = received_seq_number;
						window_receive[received_seq_number % WIN_DIMENSION].data_size = ntohl(rcv_pack->data_size);
						window_receive[received_seq_number % WIN_DIMENSION].status = 3;
						window_receive[received_seq_number % WIN_DIMENSION].last_one = ntohl(rcv_pack->last_one);
						window_receive[received_seq_number % WIN_DIMENSION].error_packet = ntohl(rcv_pack->error_packet);
						window_receive[received_seq_number % WIN_DIMENSION].command_packet = ntohl(rcv_pack->command_packet);
						
						printf("ntohl(rcv_pack->command_packet): %i\n", ntohl(rcv_pack->command_packet));
						
						printf("command 1: %i\n", window_receive[received_seq_number % WIN_DIMENSION].command_packet);
						
						memcpy(&(window_receive[received_seq_number % WIN_DIMENSION].data), rcv_pack->data, window_receive[received_seq_number % WIN_DIMENSION].data_size * sizeof(char));
						
						printf("ho copiato il pk rcv, command 2: %i\n", window_receive[received_seq_number % WIN_DIMENSION].command_packet);
					}

					if(window_receive[window_base % WIN_DIMENSION].status == 3){ // Faccio avanzare la finestra se necessario
						
						printf("status base = 3\n");
							
						while(window_receive[window_base % WIN_DIMENSION].status == 3){
							printf("avanza finestra\n");
							
							// Scrivo su file stream
							if(!(window_receive[window_base % WIN_DIMENSION].error_packet) && !(window_receive[window_base % WIN_DIMENSION].command_packet)){
								printf("sono nell if\n");
								
								scrivi_file(file, window_receive[window_base % WIN_DIMENSION].data, window_receive[window_base % WIN_DIMENSION].data_size);
							}
							
							printf("dopo stream\n");
							
							if(window_receive[window_base % WIN_DIMENSION].command_packet){
								printf("copio stringa\n");
								
								strcpy(path_receiver, (char *) &window_receive[window_base % WIN_DIMENSION].data);
							}
							
							printf("ho copiato stringa\n");
							
							window_receive[window_base % WIN_DIMENSION].status = -1;
							
							// Controllo se ho terminato la trasmissione
							if(window_receive[window_base % WIN_DIMENSION].last_one){// Ho terminato la trasmissione
								
								all_received = 1;
								
								if(!timeout_started && all_received){
									
									// Chiudo file se ho finito di scrivere
									if(path_receiver != NULL && !command_mode_receiver)
										fclose(file);
									
									// Rimouvo il file se inesistente
									if(window_receive[window_base % WIN_DIMENSION].error_packet){
										remove(path_receiver);
										
										printf("File inesistente sul server.\n");
										
										if(LOG_TO_TEXT_FILE)
											fprintf(logfile_rec, "File inesistente sul server.\n");
										
									}
									
									// Imposto timeout per gestire la chiusura della connessione
									timeout = time(NULL);
									
									// Imposto socket non bloccante
									printf("\nTutti ricevuti, imposto socket a non bloccante\n");
									
									//fcntl(sock, F_SETFL, O_NONBLOCK);
									socket_options = fcntl(sock,F_GETFL);
									socket_options = (socket_options | O_NONBLOCK);
									
									timeout_started = 1;
								}
							
							}else{
								window_base++;
							}
						}
					}
				}
				
				// Invio ACK
					
				packets_to_check_receive = ntohl(rcv_pack->packets_to_check);
				
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
				snd_pack.cli_addr = *cli_address;
				
				if(timeout_started){// Aggiorno il timeout se è già partito
					timeout = time(NULL);
				}
				
				printf("Invio ACK - seq_number: %i, data: %s\n", received_seq_number, snd_pack.data);
				
				if (sendto(sock, (char *) &snd_pack, sizeof(snd_pack), 0, (struct sockaddr *) cli_address, cli_length_receive) < 0) {
					perror("errore in sendto");
					exit(1);
				}
				
			}else{// Scarto pacchetto per loss prob
				if(received_seq_number != -1){
					printf("scarto PK %i per loss_prob\n", received_seq_number);
					
					if(LOG_TO_TEXT_FILE)
						fprintf(logfile_rec, "Scarto PK%i per loss prob. rand_for_loss: %f\n", received_seq_number, rand_for_loss);
				
					printf("ho scritto su log pk scartato\n");
				
				}
			}
		}
		
		if(timeout_started){
		
			diff_t = difftime(time(NULL), timeout);
			
			// Conversione in msec
			diff_t = diff_t * 1000;

			// Aspetto per un tempo pari al triplo del timeout e fino ad avere un solo pacchetto da riscontrare
			if(diff_t >= 10 * TIMEOUT && packets_to_check_receive == 1){// Tempo scaduto
				
				printf("receiver_thread_func - Termino (timeout)\n");

				// Termino thread
				pthread_exit(NULL);
			}
		}
	}

	printf("receiver_thread_func - Termino (fuori while)\n");

	// Termino thread
	pthread_exit(NULL);
}
