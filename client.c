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
void inizializzazionePadre(int *socket_father);
void inizializzazioneFiglio(int *socket_child);
void serviRichiesta(int sock_father, int sock_child, char *buff);

int main(int argc, char *argv[ ]){
	
	int sock_f;
	int sock_child;

	inizializzazionePadre(&sock_f);
	
	printf("Client init completed\n");
	
	memset(buff_comm, ' ', sizeof(*buff_comm));
	
	printf("Enter the IP address of the server: ");
	gets(buff_comm);

	/* assegna l'indirizzo del server prendendolo dalla riga di comando. L'indirizzo è una stringa da convertire in intero secondo network byte order. */
	if (inet_pton(AF_INET, buff_comm, &server_addr.sin_addr) <= 0){/* inet_pton (p=presentation) vale anche per indirizzi IPv6 */
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

	serviRichiesta(sock_f, sock_child, buff_comm);
	
	//Chiudo socket del figlio
	if(close(sock_child) < 0){
		perror("errore in socket");
		exit(-1);
	}
	
	exit(0);
}

void inizializzazionePadre(int *socket_father){

	buff_comm = (char *) malloc(MAX_PK_DATA_SIZE);

	//Per entrare nel while del padre
	//NOTA: fork restituisce 0 al figlio, pid del figlio al padre
	pid = 1;	
	
	//Creo socket
	//AF_INET = IPv4
	//SOCK_DGRAM = servizio senza connessione
	//0 = default protocol
	if ((*socket_father = socket(AF_INET, SOCK_DGRAM, 0)) < 0) { /* crea il socket */
		perror("errore in socket");
		exit(-1);
	}

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

void serviRichiesta(int sock_father, int sock_child, char *buff){
	
	//Chiudo socket del padre
	if(close(sock_father) < 0){
		perror("errore in socket");
		exit(-1);
	}

	int command;
	command = -1;

	command = seleziona_comando(buff);
	esegui_comando(buff, command, sock_child, server_addr);
}
