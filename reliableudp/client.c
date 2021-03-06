#include <sys/types.h> 
#include <sys/socket.h> 
#include <arpa/inet.h>

#include <unistd.h> 
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "commands_client.c"

// Per gestire il fork
pid_t pid;

// Per indirizzo del server
struct sockaddr_in server_addr;

// Buffer per lettura comando
char *buff_comm;

//Prototipi
void inizializzazionePadre_client();
void inizializzazioneFiglio(int *socket_child);
void serviRichiesta_client(int sock_child, char *buff);

int main(int argc, char *argv[ ]){
	
	int sock_child;

	inizializzazionePadre_client();
	
	printf("Client init completed\n");
	
	memset(buff_comm, ' ', sizeof(*buff_comm));
		
	printf("Enter the IP address of the server: ");
	gets(buff_comm);

	/* assegna l'indirizzo del server prendendolo dalla riga di comando. L'indirizzo è una stringa da convertire in intero secondo network byte order. */
	if (inet_pton(AF_INET, buff_comm, &server_addr.sin_addr) <= 0){
		fprintf(stderr, "errore in inet_pton per %s\n", buff_comm);
		exit(1);
	}
	
	memset(buff_comm, ' ', sizeof(*buff_comm));

	
	while(pid != 0){

		printf("Enter command: ");
		gets(buff_comm);

		//Creo figlio
		pid = fork();
	}

	inizializzazioneFiglio(&sock_child);

	serviRichiesta_client(sock_child, buff_comm);
	
	//Chiudo socket del figlio
	if(close(sock_child) < 0){
		perror("errore in socket");
		exit(-1);
	}
	
	exit(0);
}

void inizializzazionePadre_client(){

	buff_comm = (char *) malloc(MAX_PK_DATA_SIZE);

	//Per entrare nel while del padre
	//NOTA: fork restituisce 0 al figlio, pid del figlio al padre
	pid = 1;

	//struct per indirizzo di rete del server
	memset((void *)&server_addr, 0, sizeof(server_addr));

	server_addr.sin_family = AF_INET;
	
	//Numero di porta del server
	//htons = host to network byte order short
	server_addr.sin_port = htons(SERV_PORT);
}

void inizializzazioneFiglio(int *socket_child){
	if ((*socket_child = socket(AF_INET, SOCK_DGRAM, 0)) < 0) {
		perror("errore in socket");
		exit(1);
	}
}

void serviRichiesta_client(int sock_child, char *buff){

	int command;
	command = -1;

	command = seleziona_comando(buff);
	esegui_comando(buff, command, sock_child, &server_addr);
}
