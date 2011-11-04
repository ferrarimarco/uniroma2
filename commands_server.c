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


void com_get(char *buff, int sock_child, struct sockaddr_in *receiver_addr);
void com_put(char *buff, int sock_child, struct sockaddr_in sender_addr);
void com_list(int sock_child, struct sockaddr_in receiver_addr);

int seleziona_comando(char *buff){
	
	if(buff[0] == 'G') //GET
		return 0;
	else if(buff[0] == 'P') //PUT
		return 1;
	else if(buff[0] == 'L') //LIST
		return 2;
}

void esegui_comando(char *buff, int command, int sock_child, struct sockaddr_in *addr){
	
	printf("esegui_comando - command: %i\n", command);
	
	switch(command){
		case 0:
			printf("esegui_comando: seleziono get\n");
			com_get(buff, sock_child, addr);
			break;
		case 1:
			com_put(buff, sock_child, *addr);
			break;
		case 2: 
			com_list(sock_child, *addr);
			break;
    };
}

void com_get(char *buff, int sock_child, struct sockaddr_in *receiver_addr){

	printf("com_get_server - ricevuto command: %s\n", buff);
	
	//Per path del file
	char *file_name;
	char delims[] = " ";
	char *path;

	//Mi muovo fino all'inizio del nome file
	file_name = strtok(buff, delims);
	file_name = strtok(NULL, delims);

	path = malloc(snprintf(NULL, 0, "%s%s", SERVER_SHARE_PATH, file_name) + 1);
	sprintf(path, "%s%s", SERVER_SHARE_PATH, file_name);
	strcat(path, "\0");
	
	send_data(path, sock_child, receiver_addr, 0);
}

void com_put(char *buff, int sock_child, struct sockaddr_in sender_addr){

	printf("com_put_server - ricevuto command: %s\n", buff);
	
	char *temp;
	temp = malloc(snprintf(NULL, 0, "%s", buff));
	
	strcpy(temp, buff + 4);

	char *path;
	
	path = malloc(snprintf(NULL, 0, "%s%s", SERVER_SHARE_PATH, temp) + 1);
	sprintf(path, "%s%s", SERVER_SHARE_PATH, temp);
	strcat(path, "\0");
	
	//send_data(path, sock_child, &sender_addr, 1);
	
	// Invia al client un pacchetto per comunicare l'indirizzo del server figlio
	if (sendto(sock_child, NULL, 0, 0, (struct sockaddr *) &sender_addr, sizeof(sender_addr)) < 0){
		perror("errore in sendto");
		exit(1);
	}
	
	receive_data(path, sock_child, &sender_addr, 0);
	
}

void com_list(int sock_child, struct sockaddr_in receiver_addr){

	printf("com_list_server - ricevuto command: LIST\n");

	FILE *temp_file_list;
	char *temp_file_name;
	
	// Creo un file temporaneo
	temp_file_name = tmpnam(NULL);
	temp_file_list = fopen(temp_file_name, "w");

	lista_file(temp_file_list);
	
	// Chiudo il file temp
	fclose(temp_file_list);	
	
	// Invio il file temp
	send_data(temp_file_name, sock_child, &receiver_addr, 0);
	
	// Rimuovo il file temp
	remove(temp_file_name);
}