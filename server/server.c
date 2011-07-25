#include "server_base.h"
#include "lock.c"

static pid_t *pids;

int main(int argc, char *argv[ ])
{
	int sockfd;
	int i;
	int len;
	int listenfd;
	socklen_t addrlen;
	struct sockaddr_in addr;
	char buff[MAXLINE];

	if ((sockfd = socket(AF_INET, SOCK_DGRAM, 0)) < 0) { /* crea il socket */
		perror("errore in socket");
		exit(-1);
	}

	memset((void *)&addr, 0, sizeof(addr));
	addr.sin_family = AF_INET;
	addr.sin_addr.s_addr = htonl(INADDR_ANY); /* il server accetta pacchetti su una qualunque delle sue interfacce di rete */
	addr.sin_port = htons(SERV_PORT); /* numero di porta del server */

	/* assegna l'indirizzo al socket */
	if (bind(sockfd, (struct sockaddr *) &addr, sizeof(addr)) < 0) { 
		perror("errore in bind");
		exit(-1);
	}

	pids = calloc(N_CHILDREN, sizeof(pid_t));
	my_lock_init("/tmp/lock.XXXXXX"); /* un file di lock per tutti i processi child */

	for (i = 0; i < N_CHILDREN; i++){
		pids[i] = child_make(i, listenfd, addrlen);
	}
}


void web_child(){
	

}

void child_main(int i, int listenfd, int addrlen)
{
	int	connfd;
	socklen_t clilen;
	struct sockaddr *cliaddr;

	cliaddr = malloc(addrlen);
	printf("child %ld starting\n", (long) getpid());

	for ( ; ; ) {
		clilen = addrlen;
		my_lock_wait();	/* my_lock_wait() usa fcntl() */
		
		connfd = accept(listenfd, cliaddr, &clilen);
		
		my_lock_release();

		web_child(connfd); /* processa la richiesta */
		
		close(connfd);
	}
}

pid_t child_make(int i, int listenfd, int addrlen)
{
	pid_t pid;

	if ( (pid = fork()) > 0)
		return(pid); /* processo padre */
	
	child_main(i, listenfd, addrlen); /* non ritorna mai */
}


