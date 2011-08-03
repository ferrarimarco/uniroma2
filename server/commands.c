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

int seleziona_comando(char *buff, int *rec_data_amount){

	// aggiunge il carattere di terminazione
	buff[*rec_data_amount] = 0;
	
	if(buff[0] == 'G')
		return 0;
	else if(buff[0] == 'P')
		return 1;
	else if(buff[0] == 'L')
		return 2;
}

void esegui_comando(char *buff, int *received_data_amount, int command, int sock_child, struct sockaddr_in cli_addr){
	
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
    };


}

void com_get(char *buff, int *rec_data_amount, int sock_child, struct sockaddr_in cli_addr){

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

void com_put(){}

void com_list(int sock_child, struct sockaddr_in cli_addr){

	int length;
	length = 10000;
	char file_list[10000];

	length = lista_file(file_list);

	invia_stringa(file_list, sock_child, cli_addr);
}