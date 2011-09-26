#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <errno.h>

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>

#include "file_io.c"
#include "send_and_receive.c"


void com_get(char *buff, int *rec_data_amount, int sock_child, struct sockaddr_in cli_addr);
void com_put();
void com_list(int sock_child, struct sockaddr_in cli_addr);
void com_time(int sock_child, struct sockaddr_in cli_addr);

int seleziona_comando(char *buff, int *rec_data_amount){

	// aggiunge il carattere di terminazione
	buff[*rec_data_amount] = 0;
	
	//PREVEDI CASO ACK

	if(buff[0] == 'G')
		return 0;
	else if(buff[0] == 'P')
		return 1;
	else if(buff[0] == 'L')
		return 2;
	else if(buff[0] == 'T')
		return 3;
}

void esegui_comando(char *buff, int *received_data_amount, int command, int sock_child, struct sockaddr_in cli_addr){
	
	printf("buffer comando: %s\n", buff);

	switch(command) {
		case 0:
			com_get(buff, received_data_amount, sock_child, cli_addr);
			break;
		case 1:
			com_put();
			break;
		case 2: 
			com_list(sock_child, cli_addr);
			break;
		case 3:
			com_time(sock_child, cli_addr);
			break;
    };
}

void com_get(char *buff, int *rec_data_amount, int sock_child, struct sockaddr_in cli_addr){

	//Per path del file
	char *file_name;
	char delims[] = " ";

	//Per memorizzare file
	char *buffer_file;
	char *path;

	int i;

	//char *path = "/media/sf_reliable_udp/server/share/pac03.nfo\0";


	//Controllo se il comando è ben formato
	if(buff[0] == 'G' && buff[1] == 'E' && buff[2] == 'T' && buff[3] == ' '){

		//Mi muovo fino all'inizio del nome file
		file_name = strtok(buff, delims);
		file_name = strtok(NULL, delims);

		path = malloc(snprintf(NULL, 0, "%s%s", SERVER_SHARE_PATH, file_name) + 1);
		sprintf(path, "%s%s", SERVER_SHARE_PATH, file_name);

		leggi_file(path, &buffer_file);
		invia_stringa(buffer_file, sock_child, cli_addr);

		//Libero memoria
		free(buffer_file);
		free(path);

	}else{
		printf("comando mal formato!");
		//INVIARE PACCHETTO CONTENENTE STRINGA VUOTA (CONDIZIONE DI ERRORE)	
	}

	//Leggi file in buffer

	//Invia file


}

void com_put(){}

void com_time(int sock_child, struct sockaddr_in cli_addr){
	
	char buff_send[MAXLINE];

	//Test con time
	time_t ticks;
	ticks = time(NULL);
    
	/* scrive in buff l'orario nel formato ottenuto da ctime; snprintf impedisce l'overflow del buffer. */
    snprintf(buff_send, sizeof(buff_send), "%.24s\r\n", ctime(&ticks)); /* ctime trasforma la data e l’ora da binario in ASCII. \r\n per carriage return e line feed*/

	/* scrive sul socket il contenuto di buff */
    if (sendto(sock_child, buff_send, strlen(buff_send), 0, (struct sockaddr *) &cli_addr, sizeof(cli_addr)) < 0) {
      perror("errore in sendto");
      exit(-1);
    }
}

void com_list(int sock_child, struct sockaddr_in cli_addr){

	int length;
	length = 10000;
	char file_list[10000];

	length = lista_file(file_list);

	invia_stringa(file_list, sock_child, cli_addr);
}