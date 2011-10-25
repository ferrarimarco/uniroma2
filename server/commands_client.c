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
#include "send.c"
#include "receive.c"


//Per indirizzo del server
struct sockaddr_in child_server_addr;

void com_get(char *buff, int sock_child, struct sockaddr_in sender_addr);
void com_put(char *buff, int sock_child, struct sockaddr_in receiver_addr);
void com_list(char *buff, int sock_child, struct sockaddr_in sender_addr);

int seleziona_comando(char *buff){
	
	if(buff[0] == 'G') // Ricezione
		return 0;
	else if(buff[0] == 'P') // Invio
		return 1;
	else if(buff[0] == 'L') // Lista file
		return 2;
}

void esegui_comando(char *buff, int command, int sock_child, struct sockaddr_in cli_addr){
	
	switch(command){
		case 0:
			com_get(buff, sock_child, cli_addr);
			break;
		case 1:
			com_put(buff, sock_child, cli_addr);
			break;
		case 2: 
			com_list(buff, sock_child, cli_addr);
			break;
    };
}

void com_get(char *buff, int sock_child, struct sockaddr_in sender_addr){
	
	/* Invia al server il pacchetto di richiesta*/
	if (sendto(sock_child, buff, MAX_PK_DATA_SIZE, 0, (struct sockaddr *) &sender_addr, sizeof(sender_addr)) < 0){
		perror("errore in sendto");
		exit(1);
	}

	char *temp;
	temp = malloc(snprintf(NULL, 0, "%s", buff));
	
	strcpy(temp, buff + 4);

	char *path;
	
	path = malloc(snprintf(NULL, 0, "%s%s", CLIENT_SAVE_PATH, temp) + 1);
	sprintf(path, "%s%s", CLIENT_SAVE_PATH, temp);
	strcat(path, "\0");	
	
	receive_data(path, sock_child, sender_addr);
	
}

void com_put(char *buff, int sock_child, struct sockaddr_in receiver_addr){
	
	//Per path del file
	char *file_name;
	char delims[] = " ";
	char *path;
	
	char *ricezione;
	ricezione = malloc(MAXLINE);
	
	//struct per indirizzo di rete
	memset((void *)&child_server_addr, 0, sizeof(child_server_addr));
	child_server_addr.sin_family = AF_INET;

	int i;
	
	/* Invia al server il pacchetto di richiesta*/
	if (sendto(sock_child, buff, MAX_PK_DATA_SIZE, 0, (struct sockaddr *) &receiver_addr, sizeof(receiver_addr)) < 0){
		perror("errore in sendto");
		exit(1);
	}
	
	int child_server_addr_length = sizeof(child_server_addr);
	
	//Ricevo dati dal socket
	recvfrom(sock_child, ricezione, MAXLINE, 0, (struct sockaddr *) &child_server_addr, &child_server_addr_length);

	
	// ATTENZIONE: il destinatario del prec send_to è il server padre
	// Il destinatario (param si send_data) deve essere il server figlio, quindi devo inviare un ack dal server figlio
	
	//Controllo se il comando è ben formato
	if(buff[0] == 'P' && buff[1] == 'U' && buff[2] == 'T' && buff[3] == ' '){

		//Mi muovo fino all'inizio del nome file
		file_name = strtok(buff, delims);
		file_name = strtok(NULL, delims);

		path = malloc(snprintf(NULL, 0, "%s%s", CLIENT_SHARE_PATH, file_name) + 1);
		sprintf(path, "%s%s", CLIENT_SHARE_PATH, file_name);
		strcat(path, "\0");
		
		// CONTROLLARE SE IL FILE ESISTE!

		send_data(path, sock_child, child_server_addr);
		
	}else{
		printf("comando mal formato!");
		//INVIARE PACCHETTO CONTENENTE ERRORE
	}
	
}

void com_list(char *buff, int sock_child, struct sockaddr_in sender_addr){
	
	/* Invia al server il pacchetto di richiesta*/
	if (sendto(sock_child, buff, MAX_PK_DATA_SIZE, 0, (struct sockaddr *) &sender_addr, sizeof(sender_addr)) < 0){
		perror("errore in sendto");
		exit(1);
	}
	
	receive_data(NULL, sock_child, sender_addr);
	
}
