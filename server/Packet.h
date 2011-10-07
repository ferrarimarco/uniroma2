#include "constants.h"
#include <netinet/in.h>

typedef struct {
	
	int seq_number;
	
	// -1 = in buffer/window but empty, 0 = not in window (buffered only), 1 = not transmitted but in window, 2 = transmitted, 3 = transmitted and ACKed
	int status;
	
	// To hold data
	unsigned char data[MAX_PK_DATA_SIZE];
	
	// Destination child socket
	int sock_child;
	
	// Destination address
	struct sockaddr_in cli_addr;
	
} PACKET;

