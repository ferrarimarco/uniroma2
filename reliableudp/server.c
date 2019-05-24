#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <errno.h>

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>

#include <ftw.h>
#include <sys/stat.h>

#include "commands_server.c"

#include "constants.h"

//Per indirizzo del server
struct sockaddr_in addr;

//Per indirizzo del client
struct sockaddr_in cli_addr;

//Prototipi
void inizializzazionePadre(int *s);
void inizializzazioneFiglio(int *child, in_port_t client_port, in_addr_t client_address);
void serviRichiesta_server(int sockfd, int sock_child, char *buff);

int main(int argc, char *argv[ ]){
	int sockfd;
	int sock_child;
	int rec_data_amount;

	pid_t pid;
	//char buff[MAX_PK_DATA_SIZE];
	char *buff;
	buff = (char *) malloc(MAX_PK_DATA_SIZE);

	//Variabili indirizzo client
	in_port_t client_port;
	in_addr_t client_ip;
	int clientaddr;
	clientaddr = 0;
	
	int server_addr_length = sizeof(addr);

	inizializzazionePadre(&sockfd);

	//Per entrare nel while
	//NOTA: fork restituisce 0 al figlio, pid del figlio al padre
	pid = 1;

	printf("Server init completed\n");

	printf("server: socket padre = %i\n", sockfd);
	printf("main - Indirizzo padre udp://%s:%u\n", inet_ntoa(addr.sin_addr), ntohs(addr.sin_port));
	
	while(pid != 0){
	
		//Ricevo dati dal socket
		//recvfrom(sockfd, buff, MAXLINE, 0, (struct sockaddr *) &cli_addr, &server_addr_length);
		receive_data(buff, sockfd, &cli_addr, 1);
		printf("server padre: ricevuto comando %s\n", buff);
		
		//Prendo indirizzo del client
		client_ip = cli_addr.sin_addr.s_addr;
		client_port = cli_addr.sin_port;

		//network to host short
		clientaddr = ntohs(cli_addr.sin_addr.s_addr);
		
		//Creo figlio
		pid = fork();
		inizializzazioneFiglio(&sock_child, client_port, client_ip);
		
		if(pid != 0)
			printf("server: socket figlio = %i\n", sock_child);

	}
	
	printf("main - Indirizzo figlio udp://%s:%u\n", inet_ntoa(addr.sin_addr), ntohs(addr.sin_port));
	printf("main - Indirizzo client udp://%s:%u\n", inet_ntoa(cli_addr.sin_addr), ntohs(cli_addr.sin_port));
	
	printf("Ricevuto comando: %s\n", buff);

	serviRichiesta_server(sockfd, sock_child, buff);

	//Chiudo socket del figlio
	if(close(sock_child) < 0){
		perror("errore in socket");
		exit(-1);
	}
	
	exit(0);
}

void inizializzazionePadre(int *s){

	//Creo socket
	//AF_INET = IPv4
	//SOCK_DGRAM = servizio senza connessione
	//0 = default protocol
	if ((*s = socket(AF_INET, SOCK_DGRAM, 0)) < 0) { /* crea il socket */
		perror("errore in socket");
		exit(-1);
	}

	//struct per indirizzo di rete
	memset((void *)&addr, 0, sizeof(addr));

	addr.sin_family = AF_INET;

	//Server accetta pacchetti su una qualunque delle sue interfacce di rete
	//htonl = host to network byte order long
	addr.sin_addr.s_addr = htonl(INADDR_ANY);

	//Numero di porta del server
	//htons = host to network byte order short
	addr.sin_port = htons(SERV_PORT);

	//Assegno indirizzo al socket
	if (bind(*s, (struct sockaddr *) &addr, sizeof(addr)) < 0) {
		perror("errore in bind");
		exit(-1);
	}
}

void inizializzazioneFiglio(int *child, in_port_t client_p, in_addr_t client_add){

	if ((*child = socket(AF_INET, SOCK_DGRAM, 0)) < 0) {
		perror("errore in socket");
		exit(1);
	}

	memset((void *) &cli_addr, 0, sizeof(cli_addr));
	cli_addr.sin_family = AF_INET;
	cli_addr.sin_addr.s_addr = client_add; 
	cli_addr.sin_port = client_p;

	//Numero di porta del server (figlio)
	//htons = host to network byte order short
	addr.sin_port = htons(0);

	//Assegno indirizzo al socket
	if (bind(*child, (struct sockaddr *) &addr, sizeof(addr)) < 0) {
		perror("errore in bind");
		exit(-1);
	}

}

void serviRichiesta_server(int sockfd, int sock_child, char *buff){

	//Chiudo socket del padre
	if(close(sockfd) < 0){
		perror("errore in socket");
		exit(-1);
	}
	
	strcat(buff, "\0");
	printf("server - comando ricevuto ed inserito terminatore: %s\n", buff);
	
	int command;
	command = -1;

	command = seleziona_comando(buff);
	printf("server - comando selezionato: %i\n", command);
	
	esegui_comando(buff, command, sock_child, &cli_addr);
	
	printf("server - comando eseguito\n");
}