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


void com_get(char *buff, int sock_child, struct sockaddr_in cli_addr);
void com_put();
void com_list(int sock_child, struct sockaddr_in cli_addr);
void com_time(int sock_child, struct sockaddr_in cli_addr);

int seleziona_comando(char *buff){
	
	if(buff[0] == 'G') //GET
		return 0;
	else if(buff[0] == 'P') //PUT
		return 1;
	else if(buff[0] == 'L') //LIST
		return 2;
}

void esegui_comando(char *buff, int command, int sock_child, struct sockaddr_in cli_addr){
	
	switch(command){
		case 0:
			com_get(buff, sock_child, cli_addr);
			break;
		case 1:
			com_put();
			break;
		case 2: 
			com_list(sock_child, cli_addr);
			break;
    };
}

void com_get(char *buff, int sock_child, struct sockaddr_in cli_addr){

	//Per path del file
	char *file_name;
	char delims[] = " ";
	char *path;

	int i;

	//Controllo se il comando è ben formato
	if(buff[0] == 'G' && buff[1] == 'E' && buff[2] == 'T' && buff[3] == ' '){

		//Mi muovo fino all'inizio del nome file
		file_name = strtok(buff, delims);
		file_name = strtok(NULL, delims);

		path = malloc(snprintf(NULL, 0, "%s%s", SERVER_SHARE_PATH, file_name) + 1);
		sprintf(path, "%s%s", SERVER_SHARE_PATH, file_name);
		strcat(path, "\0");
		
		// CONTROLLARE SE IL FILE ESISTE!

		send_data(path, sock_child, cli_addr);
		
	}else{
		printf("comando mal formato!");
		//INVIARE PACCHETTO CONTENENTE ERRORE
	}
}

void com_put(){}

void com_list(int sock_child, struct sockaddr_in cli_addr){

	int length;
	length = 10000;
	char file_list[10000];

	length = lista_file(file_list);

	//invia_stringa(file_list, sock_child, cli_addr);
}