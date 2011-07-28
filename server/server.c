#include "server_base.h"

//Per indirizzo del server
struct sockaddr_in addr;

//Per indirizzo del client
struct sockaddr_in cli_addr;

//Prototipi
void inizializzazionePadre(int *s , socklen_t *l, socklen_t *cl);
void inizializzazioneFiglio(int *child, in_port_t client_port, in_addr_t client_address);

int main(int argc, char *argv[ ])
{
	int sockfd;
	int sock_child;
	int i;

	socklen_t len;
	socklen_t clen;
	pid_t pid;
	char buff[MAXLINE];

	//Variabili indirizzo client
	in_port_t client_port;
	in_addr_t client_ip;
	int clientaddr;
	clientaddr = 0;


	inizializzazionePadre(&sockfd, &len, &clen);

	//Per entrare nel while
	//NOTA: fork restituisce 0 al figlio, pid del figlio al padre
	pid = 1;

	printf("Server Ready!\n");

	printf("PADRE: getpid = %d\n", getpid());
	printf("PADRE: getppid = %d\n", getppid());

	while(pid != 0){

		printf("sono bloccato su recvfrom, chi sono (pid) = %d, ppid = %d\n", getpid(), getppid());

		//Ricevo dati dal socket
		if (recvfrom(sockfd, buff, MAXLINE, 0, (struct sockaddr *)&cli_addr, &clen) < 0) {
			perror("errore in recvfrom");
			exit(-1);
		}

		printf("ho passato recvfrom, chi sono (pid) = %d, ppid = %d\n", getpid(), getppid());

		//Prendo indirizzo del client
		client_ip = cli_addr.sin_addr.s_addr;
		client_port = cli_addr.sin_port;

		//network to host short
		clientaddr = ntohs(cli_addr.sin_addr.s_addr);
		
		printf("creo figlio, chi sono (pid) = %d, ppid = %d\n", getpid(), getppid());
		printf("variabile pid prima di fork = %d, chi sono (pid) = %d, ppid = %d\n", pid, getpid(), getppid());
		
		//Creo figlio
		pid = fork();
		printf("variabile pid dopo di fork = %d, chi sono (pid) = %d, ppid = %d\n", pid, getpid(), getppid());
	}

	printf("chi sono (pid) = %d, ppid = %d\n", getpid(), getppid());
	
	inizializzazioneFiglio(&sock_child, client_port, client_ip);
	
	printf("Figlio inizializzato. chi sono (pid) = %d, ppid = %d\n", getpid(), getppid());

	//Chiudo socket del padre
	if(close(sockfd) < 0){
		perror("errore in socket");
		exit(-1);
	}

	printf("Chiuso socket del padre. chi sono (pid) = %d, ppid = %d\n", getpid(), getppid());

	//Test con time
	time_t ticks;

	ticks = time(NULL); /* legge l'orario usando la chiamata di sistema time */
    
	/* scrive in buff l'orario nel formato ottenuto da ctime; snprintf impedisce l'overflow del buffer. */
    snprintf(buff, sizeof(buff), "%.24s\r\n", ctime(&ticks)); /* ctime trasforma la data e l’ora da binario in ASCII. \r\n per carriage return e line feed*/
    
	printf("Preparati dati per l'invio. chi sono (pid) = %d, ppid = %d\n", getpid(), getppid());

	/* scrive sul socket il contenuto di buff */
    if (sendto(sock_child, buff, strlen(buff), 0, (struct sockaddr *) &cli_addr, sizeof(cli_addr)) < 0) {
      perror("errore in sendto");
      exit(-1);
    }
	
	printf("dati inviati, esco. chi sono (pid) = %d, ppid = %d\n", getpid(), getppid());

	exit(0);
}

void inizializzazionePadre(int *s , socklen_t *l, socklen_t *cl){

	*l = sizeof(addr);
	*cl = sizeof(cli_addr);

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
}

void serviRichiesta(){}