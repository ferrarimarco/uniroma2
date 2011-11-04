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
#include "Time_Packet.h"

// Threads
pthread_t coordinator_thread;
pthread_t sender_thread;
pthread_t file_reader_thread;
pthread_t ack_checker_thread;
pthread_t timeout_watcher_thread;

// Conditions
pthread_cond_t send_condition = PTHREAD_COND_INITIALIZER;
pthread_cond_t file_read_condition = PTHREAD_COND_INITIALIZER;
pthread_cond_t timeout_condition = PTHREAD_COND_INITIALIZER;

// Data Mutexes
pthread_mutex_t transm_complete_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t buffer_read_file_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t available_window_space_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t available_buffer_space_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t window_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t timeout_queue_mutex = PTHREAD_MUTEX_INITIALIZER;

// Thread coordination
int transmission_complete;
int file_read_complete;
int all_acked;
int available_buffer_space;
int available_window_space;

// Transmission management
int sock;
struct sockaddr_in *cli_address;
int cli_length_send;
int command_mode;

// Transmission window management
int needed_packets;
PACKET window_send[WIN_DIMENSION];
int next_sequence_number;
TIME_PACKET timeout_queue[WIN_DIMENSION];
int packets_to_check_send;

// File read management
FILE *file;
unsigned char *temp_read_file;
PACKET buffer_read_file[BUFFER_FILE_DIMENSION];

// File log sender
FILE *logfff;

// Prototypes
void *coordinator_thread_func(void *arg);
void *sender_thread_func(void *arg);
void *file_reader_thread_func(void *arg);
void *ack_checker_thread_func(void *arg);
void *timeout_watcher_thread_func(void *arg);

int debug = 0;
int debug_timeout_watcher = 1;

void send_data(char *path, int sock_child, struct sockaddr_in *receiver_addr, int is_command){
	
	if(LOG_TO_TEXT_FILE)
		logfff = fopen(SENDER_LOG_FILE_PATH, "w");
	
	// Inizializzazione
	sock = 0;
	transmission_complete = 0;
	file_read_complete = 0;
	all_acked = 0;
	available_buffer_space = BUFFER_FILE_DIMENSION;
	available_window_space = WIN_DIMENSION;
	next_sequence_number = 0;
	packets_to_check_send = 0;
	needed_packets = -1;

	sock = sock_child;
	cli_address = receiver_addr;
	printf("send_data - Indirizzo ricevente udp://%s:%u\n", inet_ntoa(receiver_addr->sin_addr), ntohs(receiver_addr->sin_port));
	cli_length_send = sizeof(*cli_address);
	
	// Per gestire comandi
	command_mode = is_command;

	int i;
	
	// Inizializzo la finestra
	for(i = 0; i < WIN_DIMENSION ; i++){
		PACKET packet = {-1, -1, 0, WIN_DIMENSION, "0", sock, *cli_address, 0, 0, 0};
		window_send[i] = packet;
	}
	
	// Inizializzo il buffer di lettura
	for(i = 0; i < BUFFER_FILE_DIMENSION ; i++){
		PACKET packet = {-1, -1, 0, WIN_DIMENSION, "0", sock, *cli_address, 0, 0, 0};
		buffer_read_file[i] = packet;
	}
	
	// Inizializzo la finestra per gestire i timeout
	for(i = 0; i < WIN_DIMENSION; i++){
		PACKET packet = {-1, -1, 0, WIN_DIMENSION, "0", sock, *cli_address, 0, 0, 0};
		TIME_PACKET time_packet = {-1, 0.0, packet};
		timeout_queue[i] = time_packet;
	}
	
	// Conto i pacchetti necessari all'invio
	if(!is_command)
		needed_packets = file_size(path);
	

	if(LOG_TO_TEXT_FILE){
		fprintf(logfff, "send_data - Path: %s\n", path);
		fprintf(logfff, "send_data - needed packets: %i\n", needed_packets);
	}
	
	// Thread coordinatore
	void *path_pointer = (void *) path;
	
	if(pthread_create(&coordinator_thread, NULL, coordinator_thread_func, path_pointer) == EAGAIN){	
		perror("Errore (pthread_create): coordinator_thread");
		exit(1);
	}

	// Inizializzo seme per rand
	srand(time(0));
	
	// Join su thread coordintatore
	pthread_join(coordinator_thread, NULL);
}

void *coordinator_thread_func(void *arg){
	
	char *path = (char *) arg;

	
	// Creo i thread per gestire la trasmissione
	if(pthread_create(&ack_checker_thread, NULL, ack_checker_thread_func, NULL) == EAGAIN){	
		perror("Errore (pthread_create): ack_checker_thread");
		exit(1);
	}
	
	if(pthread_create(&file_reader_thread, NULL, file_reader_thread_func, (void *) path) == EAGAIN){	
		perror("Errore (pthread_create): file_reader_thread");
		exit(1);
	}

	if(pthread_create(&sender_thread, NULL, sender_thread_func, NULL) == EAGAIN){	
		perror("Errore (pthread_create): sender_thread");
		exit(1);
	}

	if(pthread_create(&timeout_watcher_thread, NULL, timeout_watcher_thread_func, NULL) == EAGAIN){	
		perror("Errore (pthread_create): timeout_watcher_thread");
		exit(1);
	}
	
	int transm_complete_coord = 0;
	
	while(transm_complete_coord != 1){
		if(debug)
		printf("blocco tcm\n");
		
		// Controllo se devo eseguire il while di nuovo
		pthread_mutex_lock(&transm_complete_mutex);
		
		transm_complete_coord = transmission_complete;

		// Controllo se ho completato la trasmissione
		if(file_read_complete && all_acked){
			transmission_complete = 1;

			if(LOG_TO_TEXT_FILE){
				fprintf(logfff, "coord_func - trasmissione completata\n");
			}
		}
		
		pthread_mutex_unlock(&transm_complete_mutex);
		
		if(debug)
		printf("sblocco tcm\n");
		
		if(debug)
		printf("blocco absm\n");

		// Controllo se devo svegliare il thread che riempie il buffer
		pthread_mutex_lock(&available_buffer_space_mutex);
		
		if(available_buffer_space >= 0){
			pthread_cond_signal(&file_read_condition);
		}
		
		pthread_mutex_unlock(&available_buffer_space_mutex);
		
		if(debug)
		printf("sblocco absm\n");
		
		if(debug)
		printf("blocco awsm\n");
		
		// Controllo se devo svegliare il thread che invia i pacchetti
		pthread_mutex_lock(&available_window_space_mutex);
		
		if(available_window_space > 0){			
			pthread_cond_signal(&send_condition);
		}
		
		pthread_mutex_unlock(&available_window_space_mutex);
		
		if(debug)
		printf("sblocco awsm\n");
		
		if(debug)
		printf("blocco absm\n");

		// Controllo se devo svegliare il thread che invia i pacchetti
		pthread_mutex_lock(&available_buffer_space_mutex);
		
		if(BUFFER_FILE_DIMENSION - available_buffer_space > 0){
			pthread_cond_signal(&send_condition);
		}
		
		pthread_mutex_unlock(&available_buffer_space_mutex);
		
		if(debug)
		printf("sblocco absm\n");
		
		if(debug)
		printf("blocco tqm\n");
		
		// Controllo se devo svegliare il thread che gestisce i timeout
		pthread_mutex_lock(&timeout_queue_mutex);

		if(packets_to_check_send > 0){
			pthread_cond_signal(&timeout_condition);
		}
		
		pthread_mutex_unlock(&timeout_queue_mutex);
		
		if(debug)
		printf("sblocco tqm\n");
	}


	printf("sender\n");
	pthread_join(sender_thread, NULL);
	printf("sender ok\n");
	
	printf("file_reader_thread\n");
	pthread_join(file_reader_thread, NULL);
	printf("file reader ok\n");
	
	printf("ack checker\n");
	pthread_join(ack_checker_thread, NULL);
	printf("ack checker ok\n");
	
	printf("timeout, provo a segnalare\n");
	pthread_cond_signal(&timeout_condition);
	pthread_join(timeout_watcher_thread, NULL);
	printf("timeout ok\n");
	
	if(LOG_TO_TEXT_FILE)
		fprintf(logfff, "coordinator_thread_func - Richiesta servita: GET %s\n", path);
	
	if(!command_mode)
		printf("coordinator_thread_func - Richiesta servita: GET %s\n", path);
	else
		printf("coordinator_thread_func - Richiesta servita: trasferito comando %s\n", path);
	
	// Termino thread
	pthread_exit(NULL);

}

void *sender_thread_func(void *arg){
	
	int transm_complete_sender = 0;
	int all_acked_sender = 0;
	int buffer_position = 0;
	int window_position = 0;
	
	int test_sender = 0;
	
	while(!transm_complete_sender){
	
		buffer_position = next_sequence_number % BUFFER_FILE_DIMENSION;
		window_position = next_sequence_number % WIN_DIMENSION;		
		
		// Controllo se devo eseguire il while di nuovo
		pthread_mutex_lock(&transm_complete_mutex);

		all_acked_sender = all_acked;

		pthread_mutex_unlock(&transm_complete_mutex);	
		
		if(!all_acked_sender){
		
			// Aspetto se finestra è piena
			pthread_mutex_lock(&available_window_space_mutex);

			while(available_window_space == 0){
				if(LOG_TO_TEXT_FILE){
					test_sender++;
					if(test_sender < 100){
						fprintf(logfff, "sender - finestra piena. aws: %i\n", available_window_space);
					}
				}
				
				pthread_cond_wait(&send_condition, &available_window_space_mutex);
			}
			
			test_sender = 0;
			
			pthread_mutex_unlock(&available_window_space_mutex);

			// Aspetto se buffer vuoto
			pthread_mutex_lock(&available_buffer_space_mutex);

			while(BUFFER_FILE_DIMENSION - available_buffer_space == 0){

				if(LOG_TO_TEXT_FILE){
					test_sender++;
					if(test_sender < 100){
						fprintf(logfff, "sender - buffer vuoto. abs: %i\n", available_buffer_space);
					}
				}

				pthread_cond_wait(&send_condition, &available_buffer_space_mutex);

			}
			
			test_sender = 0;
			
			pthread_mutex_unlock(&available_buffer_space_mutex);
			
			pthread_mutex_lock(&window_mutex);

			// Imposto stato del pacchetto (1 = non trasmesso ma in finestra)
			window_send[window_position].status = 1;
			
			// Controllo se è l'ultimo pacchetto da inviare
			if(next_sequence_number == needed_packets - 1 || needed_packets == -1){
				window_send[window_position].last_one = htonl(1);
			}
			
			// Prendo dati da buffer
			pthread_mutex_lock(&buffer_read_file_mutex);
			
			window_send[window_position].seq_number = htonl(next_sequence_number);
			window_send[window_position].data_size = htonl(buffer_read_file[buffer_position].data_size);
			window_send[window_position].error_packet = htonl(buffer_read_file[buffer_position].error_packet);
			window_send[window_position].command_packet = htonl(buffer_read_file[buffer_position].command_packet);
			memcpy(window_send[window_position].data, buffer_read_file[buffer_position].data, sizeof(buffer_read_file[buffer_position].data));
			
			// Imposto stato del pacchetto (1 = non trasmesso ma in finestra)
			buffer_read_file[buffer_position].status = 1;
			
			pthread_mutex_unlock(&buffer_read_file_mutex);
			
			// Inserisco il pacchetto nella coda dei timeout
			pthread_mutex_lock(&timeout_queue_mutex);

			// Per evitare la riconversione da network a host uso direttamente next_sequence_number
			// invece di window_send[window_position].seq_number
			timeout_queue[packets_to_check_send].seq_number = next_sequence_number;
			timeout_queue[packets_to_check_send].timeout = time(NULL);
			timeout_queue[packets_to_check_send].packet = window_send[window_position];
			
			pthread_mutex_unlock(&timeout_queue_mutex);
			
			packets_to_check_send++;

			window_send[window_position].packets_to_check = htonl(packets_to_check_send);
			
			if(LOG_TO_TEXT_FILE)
				fprintf(logfff, "sender - invio pacchetto %i\n", ntohl(window_send[window_position].seq_number));
			
			//printf("window_send[window_position].sock_child: %i\n", window_send[window_position].sock_child);
			
			// Invio pacchetto
			errno = 0;
			int send_ret = sendto(window_send[window_position].sock_child, (char *) &(window_send[window_position]), sizeof(window_send[window_position]), 0, (struct sockaddr *) &((&(window_send[window_position]))->cli_addr), cli_length_send);
			
			//printf("send_ret = %i\n", send_ret);
			//printf("sender - Error sending pk: %s, errno: %i\n", strerror(errno), errno);
			
			if(send_ret  == EAGAIN || send_ret ==  EWOULDBLOCK){
				perror("sender_thread_func - Errore in sendto");
				exit(1);
			}else{
					
				if(LOG_TO_TEXT_FILE){
					fprintf(logfff, "sender_thread_func - Pacchetto %i inviato\n", ntohl(window_send[window_position].seq_number));
				}
				
				// Imposto stato del pacchetto (2 = inviato ma non ancora riscontrato)
				window_send[window_position].status = 2;
				
				pthread_mutex_unlock(&window_mutex);
				
				// Decremento available_window_space di uno
				pthread_mutex_lock(&available_window_space_mutex);
				
				available_window_space--;
				
				if(LOG_TO_TEXT_FILE)
					fprintf(logfff, "sender - spazio libero nella finestra: %i\n", available_window_space);
					
				pthread_mutex_unlock(&available_window_space_mutex);		
				
				// Incremento available_buffer_space di uno
				pthread_mutex_lock(&available_buffer_space_mutex);
				
				available_buffer_space++;
				
				if(LOG_TO_TEXT_FILE)
					fprintf(logfff, "sender - spazio libero nel buffer: %i\n", available_buffer_space);

				pthread_mutex_unlock(&available_buffer_space_mutex);
				
				if(next_sequence_number == needed_packets - 1 || needed_packets == -1){
					fprintf(logfff, "sender_thread_func - Termino\n");
					
					// Termino thread
					pthread_exit(NULL);
					
				}else{// Per assegnare il prossimo numero di sequenza
					next_sequence_number++;	
				}
			}
		}
	}
}

void *file_reader_thread_func(void *arg){

	int transm_complete_file_reader = 0;
	int buffer_position_helper = 0;
	int buffer_position = 0;
	int file_read_complete_reader = 0;
	int file_read_amount = 0;
	int packets_read = 0;
	
	int test_file_reader = 0;
	
	char *path = (char *) arg;
	
	if(!command_mode){
		// Inizio lettura da file
		file = fopen(path, "rb");
		
		if(file == NULL){// Errore sull'apertura del file
			fprintf(logfff, "Errore: impossibile aprire file\n");

			// Inserisco i dati nel pacchetto
			pthread_mutex_lock(&buffer_read_file_mutex);
			
			buffer_read_file[0].error_packet = 1;
			
			// Imposto stato del pacchetto (0 = non nella finestra ma solo nel buffer)
			buffer_read_file[0].status = 0;
			buffer_read_file[0].data_size = 0;
			
			pthread_mutex_unlock(&buffer_read_file_mutex);
			
			// Decremento available_buffer_space di uno
			pthread_mutex_lock(&available_buffer_space_mutex);

			available_buffer_space--;

			if(LOG_TO_TEXT_FILE)
				fprintf(logfff, "file reader - spazio libero nel buffer: %i\n", available_buffer_space);
			
			pthread_mutex_unlock(&available_buffer_space_mutex);

			pthread_mutex_lock(&transm_complete_mutex);

			file_read_complete = 1;
			
			if(LOG_TO_TEXT_FILE){
				fprintf(logfff, "file_reader_thread_func - read complete!\n");
			
				fprintf(logfff, "file_read: pacchetti letti: %i\n", packets_read);
			}
			
			pthread_mutex_unlock(&transm_complete_mutex);
			
			fprintf(logfff, "file_reader_thread - termino!\n");
			
			// Termino thread
			pthread_exit(NULL);
		}
	}
	
	if(command_mode){
		fprintf(logfff, "Invio comando\n");
		
		// Inserisco i dati nel pacchetto
		pthread_mutex_lock(&buffer_read_file_mutex);
		
		// Imposto stato del pacchetto (0 = non nella finestra ma solo nel buffer)
		buffer_read_file[0].status = 0;
		buffer_read_file[0].command_packet = 1;
		buffer_read_file[0].data_size = strlen(path);
		
		strcpy((char*) &(buffer_read_file[0].data), path);
		
		pthread_mutex_unlock(&buffer_read_file_mutex);
		
		// Decremento available_buffer_space di uno
		pthread_mutex_lock(&available_buffer_space_mutex);
		
		available_buffer_space--;
		
		if(LOG_TO_TEXT_FILE)
			fprintf(logfff, "file reader - spazio libero nel buffer: %i\n", available_buffer_space);
		
		pthread_mutex_unlock(&available_buffer_space_mutex);
		
		pthread_mutex_lock(&transm_complete_mutex);
		
		file_read_complete = 1;
		
		if(LOG_TO_TEXT_FILE){
			fprintf(logfff, "file_reader_thread_func - read complete!\n");
			
			fprintf(logfff, "file_read: pacchetti letti: %i\n", packets_read);
		}
		
		pthread_mutex_unlock(&transm_complete_mutex);
		
		fprintf(logfff, "file_reader_thread - termino!\n");
		
		// Termino thread
		pthread_exit(NULL);
	}
		
	while(transm_complete_file_reader != 1){
		
		buffer_position = buffer_position_helper % BUFFER_FILE_DIMENSION;
		/*
		// Controllo se devo eseguire il while di nuovo
		pthread_mutex_lock(&transm_complete_mutex);
		
		transm_complete_file_reader = transmission_complete;
		
		// Controllo se ho completato la trasmissione
		if(transm_complete_file_reader){
			
			pthread_mutex_unlock(&transm_complete_mutex);
			
			// Chiudo file se ho finito di leggere
			fclose(file);
				
			fprintf(logfff, "file_reader_thread - termino!\n");
				
			// Termino thread
			pthread_exit(NULL);
		}
		
		pthread_mutex_unlock(&transm_complete_mutex);
		*/
		// Aspetto se non c'è spazio nel buffer
		pthread_mutex_lock(&available_buffer_space_mutex);
		
		while(available_buffer_space == 0 && !file_read_complete_reader){
			
			if(LOG_TO_TEXT_FILE){
				test_file_reader++;
				
				if(test_file_reader < 100){
					fprintf(logfff, "file reader - buffer pieno. abs: %i\n", available_buffer_space);
				}
			}
			
			pthread_cond_wait(&file_read_condition, &available_buffer_space_mutex);
		}
		
		test_file_reader = 0;
		
		pthread_mutex_unlock(&available_buffer_space_mutex);

		pthread_mutex_lock(&transm_complete_mutex);
		
		file_read_complete_reader = file_read_complete;

		pthread_mutex_unlock(&transm_complete_mutex);
		
		// Controllo se ho completato la trasmissione
		if(file_read_complete_reader){

			// Chiudo file se ho finito di leggere
			fclose(file);
				
			fprintf(logfff, "file_reader_thread_func - ho finito di leggere, termino!\n");
				
			// Termino thread
			pthread_exit(NULL);
		}

		if(!file_read_complete_reader){

			if(file_read_amount = leggi_file(file, &temp_read_file)){// Ho letto dati	
				
				if(LOG_TO_TEXT_FILE)
					fprintf(logfff, "file reader - inserisco pacchetto nel buffer\n");
				
				// Inserisco i dati nel pacchetto
				pthread_mutex_lock(&buffer_read_file_mutex);
				
				memcpy(&(buffer_read_file[buffer_position].data), temp_read_file, sizeof(buffer_read_file[buffer_position].data));

				// Imposto stato del pacchetto (0 = non nella finestra ma solo nel buffer)
				buffer_read_file[buffer_position].status = 0;
				
				buffer_read_file[buffer_position].data_size = file_read_amount;
				
				pthread_mutex_unlock(&buffer_read_file_mutex);
				
				// Aggiorno buffer_position_helper
				buffer_position_helper++;
				
				// Decremento available_buffer_space di uno
				pthread_mutex_lock(&available_buffer_space_mutex);

				available_buffer_space--;

				if(LOG_TO_TEXT_FILE)
					fprintf(logfff, "file reader - spazio libero nel buffer: %i\n", available_buffer_space);
				
				pthread_mutex_unlock(&available_buffer_space_mutex);
				
				packets_read++;
				
			}else{// Ho finito di leggere il file da inviare
				pthread_mutex_lock(&transm_complete_mutex);

				file_read_complete = 1;
				
				if(LOG_TO_TEXT_FILE){
					fprintf(logfff, "file_reader_thread_func - read complete!\n");
				
					fprintf(logfff, "file_read: pacchetti letti: %i\n", packets_read);
				}
				
				pthread_mutex_unlock(&transm_complete_mutex);
				
				// Chiudo file se ho finito di leggere
				fclose(file);
				
				fprintf(logfff, "file_reader_thread_func - ho finito di leggere, termino!\n");
				
				// Termino thread
				pthread_exit(NULL);
			}
		}
	}
}

void *ack_checker_thread_func(void *arg){
  
	// Per accogliere il pacchetto ricevuto
	PACKET *rcv_pack;

	int window_base = 0;
	int rec_from_res = 0;
	int received_seq_number = -1;
	int all_acked_ack_checker = 0;
	float rand_for_loss = 0.0;
	
	while(!all_acked_ack_checker){

		// Controllo se devo eseguire il while di nuovo
		pthread_mutex_lock(&transm_complete_mutex);

		all_acked_ack_checker = all_acked;

		pthread_mutex_unlock(&transm_complete_mutex);			
		
		if(!all_acked_ack_checker){
		
			rcv_pack = malloc(sizeof(*rcv_pack));
			rec_from_res = recvfrom(sock, (char *) rcv_pack, sizeof(*rcv_pack), 0, (struct sockaddr *) cli_address, &cli_length_send);

			if(rec_from_res == EAGAIN || rec_from_res ==  EWOULDBLOCK){
				perror("ack_checker_thread_func - Errore in recvfrom");
				exit(1);
			}else{

				received_seq_number = ntohl(rcv_pack->seq_number);
				
				if(LOG_TO_TEXT_FILE)
					fprintf(logfff, "ack_checker_thread_func - Ricevuto ACK%i\n", received_seq_number);
			
				rand_for_loss = (float) (rand() % 10) / 10;
				
				if(rand_for_loss <= (1 - LOSS_PROBABILITY)){// Pacchetto non scartato per lossprob
				
					if(received_seq_number >= window_base){// Pacchetto ricevuto è nella finestra
						
						pthread_mutex_lock(&window_mutex);
						
						if(window_send[received_seq_number % WIN_DIMENSION].status != 3){// Pacchetto ricevuto non ancora riscontrato
					
							// Imposto status a 3 (3 = pacchetto inviato e riscontrato)
							window_send[received_seq_number % WIN_DIMENSION].status = 3;
							
							pthread_mutex_lock(&timeout_queue_mutex);
							
							int u;
							
							for(u = 0; u < WIN_DIMENSION; u++){
								if(timeout_queue[u].seq_number == received_seq_number){
									
									printf("acker - riscontrato PK %i\n", received_seq_number);
									
									if(LOG_TO_TEXT_FILE)
										fprintf(logfff, "acker - riscontrato pk %i\n", received_seq_number);
									
									memmove(timeout_queue + u, timeout_queue + u + 1, sizeof(timeout_queue) - (u + 1) * sizeof(timeout_queue[u]));
									
									timeout_queue[packets_to_check_send - 1].seq_number = -1;
									
									packets_to_check_send--;
									break;
								}
							}

							pthread_mutex_unlock(&timeout_queue_mutex);
						}

						if(window_send[window_base % WIN_DIMENSION].status == 3){// Faccio avanzare la finestra
						
							while(window_send[window_base % WIN_DIMENSION].status == 3){
								
								window_send[window_base % WIN_DIMENSION].status = -1;
							
								window_base++;
								
								pthread_mutex_lock(&available_window_space_mutex);
								available_window_space++;
								pthread_mutex_unlock(&available_window_space_mutex);
								
								if(LOG_TO_TEXT_FILE)
									fprintf(logfff, "acker - riscontrata base\n");
							}
						}
						
						pthread_mutex_unlock(&window_mutex);
					}
					
				}else{// Scarto pacchetto per loss prob
					if(LOG_TO_TEXT_FILE)
						fprintf(logfff, "ack_checker_thread_func - Scarto ACK%i per loss prob. rand_for_loss: %f\n", received_seq_number, rand_for_loss);
				}
			}
					
			if(window_base >= needed_packets){// Trasmissione completata
				pthread_mutex_lock(&transm_complete_mutex);
				all_acked = 1;
				printf("acker: impost all_acked a %i\n", all_acked);
				pthread_mutex_unlock(&transm_complete_mutex);
				
				if(LOG_TO_TEXT_FILE)
					fprintf(logfff, "ack_checker_thread_func - All acked, termino\n");				

				// Termino thread
				pthread_exit(NULL);
			}
		}
	}
}

void *timeout_watcher_thread_func(void *arg){
	
	int all_acked_timeout_watcher = 0;
	int window_position = 0;
	double diff_t = 0.0;
	PACKET temp_pack;
	int send_ret;
	TIME_PACKET temp;

	/*
	// Aspetto per far partire timeout thread
	static struct timespec time_to_wait = {0, 0};
	long int a = 2L;
	pthread_cond_t toc = PTHREAD_COND_INITIALIZER;
	pthread_mutex_t mut = PTHREAD_MUTEX_INITIALIZER;
	time_to_wait.tv_sec = time(NULL) + a;
	pthread_mutex_lock(&mut);
	pthread_cond_timedwait(&toc, &mut, &time_to_wait);
	pthread_mutex_unlock(&mut);
	*/
	
	while(!all_acked_timeout_watcher){
		
		if(debug_timeout_watcher)
		printf("Timeout: blocco tcm\n");
		
		// Controllo se devo eseguire il while di nuovo
		pthread_mutex_lock(&transm_complete_mutex);

		all_acked_timeout_watcher = all_acked;
		
		printf("timeout: all_acked 1 = %i\n", all_acked);
		
		// Controllo se ho completato la trasmissione
		if(all_acked){
			pthread_mutex_unlock(&transm_complete_mutex);
			
			fprintf(logfff, "timeout_watcher_thread - Termino\n");
			printf("timeout: termino 1\n");
			// Termino thread
			pthread_exit(NULL);
		}
		
		pthread_mutex_unlock(&transm_complete_mutex);
		
		if(debug_timeout_watcher)
		printf("Timeout: sblocco tcm\n");
		
		if(debug_timeout_watcher)
		printf("Timeout: blocco tqm\n");
		
		pthread_mutex_lock(&timeout_queue_mutex);
		
		// Aspetto se non ho pacchetti da controllare
		while(packets_to_check_send == 0){
			
			if(debug_timeout_watcher)
			printf("Timeout: blocco tcm2\n");
			
			pthread_mutex_lock(&transm_complete_mutex);
			
			all_acked_timeout_watcher = all_acked;
			
			printf("timeout: all_acked 2 = %i\n", all_acked);
			
			// Controllo se ho completato la trasmissione
			if(all_acked){
				pthread_mutex_unlock(&transm_complete_mutex);
				
				if(debug_timeout_watcher)
				printf("Timeout: sblocco tcm2\n");
				
				pthread_mutex_unlock(&timeout_queue_mutex);
				
				if(debug_timeout_watcher)
				printf("Timeout: sblocco tqm\n");
				
				fprintf(logfff, "timeout_watcher_thread - Termino\n");
				printf("timeout: termino 2\n");
				// Termino thread
				pthread_exit(NULL);
			}
			
			pthread_mutex_unlock(&transm_complete_mutex);
			
			if(debug_timeout_watcher)
			printf("Timeout: sblocco tqm\n");
			
			if(LOG_TO_TEXT_FILE)
				fprintf(logfff, "file reader - wait su condizione: window empty\n");

			pthread_cond_wait(&timeout_condition, &timeout_queue_mutex);
		}
		
		if(debug_timeout_watcher)
		printf("Timeout: sblocco tcm3\n");
		
		pthread_mutex_lock(&transm_complete_mutex);
		
		all_acked_timeout_watcher = all_acked;
		
		printf("acker: all_acked 3 = %i\n", all_acked);

		// Controllo se ho completato la trasmissione
		if(all_acked){
			pthread_mutex_unlock(&transm_complete_mutex);
			
			if(debug_timeout_watcher)
			printf("Timeout: sblocco tcm3\n");
			
			pthread_mutex_unlock(&timeout_queue_mutex);
			
			if(debug_timeout_watcher)
			printf("Timeout: sblocco tqm\n");
			
			fprintf(logfff, "timeout_watcher_thread - Termino\n");
			printf("timeout: termino 3\n");
			
			// Termino thread
			pthread_exit(NULL);
		}
		
		pthread_mutex_unlock(&transm_complete_mutex);
		
		if(debug_timeout_watcher)
		printf("Timeout: sblocco tcm3\n");

		if(!all_acked_timeout_watcher){
			
			if(timeout_queue[0].seq_number != -1){// Primo pacchetto a scadere non riscontrato
				
				diff_t = 0.0;
				
				diff_t = difftime(time(NULL), timeout_queue[0].timeout);
				
				// Conversione in msec
				diff_t = diff_t * 1000;

				if(diff_t >= TIMEOUT){// Timeout scaduto
					if(LOG_TO_TEXT_FILE)
						fprintf(logfff, "timeout - timeout pacchetto %i scaduto. Reinvio\n", timeout_queue[0].seq_number);
					
					// Incremento la posizione fine della coda
					if(packets_to_check_send > 1){
						temp = timeout_queue[0];
						
						memmove(timeout_queue, timeout_queue + 1, sizeof(timeout_queue) - sizeof(timeout_queue[0]));
						
						timeout_queue[packets_to_check_send - 1] = temp;
					}
					
					// Aggiorno il timeout
					timeout_queue[packets_to_check_send - 1].timeout = time(NULL);
					
					// Per reinviare
					temp_pack = timeout_queue[packets_to_check_send - 1].packet;
					
					// Reinvio pacchetto
					send_ret = sendto(temp_pack.sock_child, (char *) &(temp_pack), sizeof(temp_pack), 0, (struct sockaddr *) &(temp_pack.cli_addr), cli_length_send);

					if(send_ret  == EAGAIN || send_ret ==  EWOULDBLOCK){
						perror("timeout_watcher_thread_func - Errore in sendto");
						exit(1);
					}
				}
			}
			
			pthread_mutex_unlock(&timeout_queue_mutex);
			
			if(debug_timeout_watcher)
			printf("Timeout: sblocco tqm\n");
		}
	}
	
	printf("timeout: sono uscito dal while\n");
	
	// Controllo se devo eseguire il while di nuovo
	pthread_mutex_lock(&transm_complete_mutex);

	all_acked_timeout_watcher = all_acked;
	
	printf("acker: all_acked 4 = %i\n", all_acked);
	
	// Controllo se ho completato la trasmissione
	if(all_acked){
		pthread_mutex_unlock(&transm_complete_mutex);
		
		printf("timeout: termino 4\n");
		
		fprintf(logfff, "timeout_watcher_thread - Termino\n");
		
		// Termino thread
		pthread_exit(NULL);
	}
	
	pthread_mutex_unlock(&transm_complete_mutex);
}
