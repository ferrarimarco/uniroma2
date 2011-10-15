#include <sys/types.h> 
#include <sys/socket.h> 
#include <arpa/inet.h>

#include <unistd.h> 
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "Packet.h"

#define SERV_PORT 5193
#define MAXLINE 1024

int main(int argc, char *argv[ ]){
	
	int sockfd, n;
	char  recvline[MAXLINE + 1];
	struct sockaddr_in servaddr;
	
	if (argc < 3){/* controlla numero degli argomenti */
		fprintf(stderr, "utilizzo: client <indirizzo IP server> <comando> [<path>]\n");
		exit(1);
	}
	
	if ((sockfd = socket(AF_INET, SOCK_DGRAM, 0)) < 0){/* crea il socket */
		perror("errore in socket");
		exit(1);
	}
	
	memset((void *)&servaddr, 0, sizeof(servaddr)); /* azzera servaddr */
	servaddr.sin_family = AF_INET; /* assegna il tipo di indirizzo */
	servaddr.sin_port = htons(SERV_PORT); /* assegna la porta del server */
	
	/* assegna l'indirizzo del server prendendolo dalla riga di comando. L'indirizzo è una stringa da convertire in intero secondo network byte order. */
	if (inet_pton(AF_INET, argv[1], &servaddr.sin_addr) <= 0){/* inet_pton (p=presentation) vale anche per indirizzi IPv6 */
		fprintf(stderr, "errore in inet_pton per %s", argv[1]);
		exit(1);
	}
	
	char *buff_comm;
	
	buff_comm = (char *) malloc(MAX_PK_DATA_SIZE);
	
	strcat(buff_comm, argv[2]);
	
	strcat(buff_comm, " ");
	
	if(argc == 4){// Ho anche il nome del file
		strcat(buff_comm, argv[3]);
	}
	
	// Termino la stringa
	strcat(buff_comm, " \0");
	
	printf("COMMAND: %s\n", buff_comm);
	
	/* Invia al server il pacchetto di richiesta*/
	if (sendto(sockfd, buff_comm, MAX_PK_DATA_SIZE, 0, (struct sockaddr *) &servaddr, sizeof(servaddr)) < 0){
		perror("errore in sendto");
		exit(1);
	}
	
	PACKET *pack;
	pack = malloc(sizeof(*pack));
	int clen = sizeof(servaddr);
	
	while(recvfrom(sockfd, (char *) pack, sizeof(*pack), 0, (struct sockaddr *) &servaddr, &clen) > 0){
		
		//pack->seq_number = ntohl(pack->seq_number);
		
		char *buff_ack;
		buff_ack = (char *) malloc(MAX_PK_DATA_SIZE);
		strcat(buff_ack, "ACK");
		sprintf(buff_ack, "%s%i", buff_ack, pack->seq_number);
		strcat(buff_ack, "\0");
		
		//printf("SizeOfPacket: %u \n", sizeof(*pack));
		printf("Seq number: %u \n", pack->seq_number);
		//printf("Sock_child: %u \n", pack->sock_child);
		//printf("Data: %s \n", pack->data);
		
		PACKET packet;
		packet.seq_number = pack->seq_number;
		packet.status = 2;
		strcpy(packet.data, buff_ack);
		packet.sock_child = sockfd;
		packet.cli_addr = servaddr;
		printf("buff_ack: %s\n", buff_ack);
		if (sendto(sockfd, (char *) &packet, sizeof(packet), 0, (struct sockaddr *) &servaddr, sizeof(servaddr)) < 0) {
			perror("errore in sendto");
			exit(1);
		}
		
	}
	
	exit(0);
}
