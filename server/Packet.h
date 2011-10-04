#include "constants.h"
#include <netinet/in.h>

typedef struct {
	
	int seq_number;
	
	// 0 = not transmitted, 1 = transmitted, 2 = transmitted and ACKed
	int status;
	
	// To hold data
	unsigned char data[MAX_PK_DATA_SIZE];
	
	// Destination child socket
	int sock_child;
	
	// Destination address
	struct sockaddr_in cli_addr;
	
} PACKET;

