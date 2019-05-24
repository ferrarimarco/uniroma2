#include "constants.h"
#include <netinet/in.h>

typedef struct {
	
	int seq_number;
	
	// -1 = in buffer/window but empty, 0 = not in window (buffered only), 1 = not transmitted but in window, 2 = transmitted/received, 3 = transmitted/received and ACKed
	int status;
	
	// To know how much data to read in the data array
	int data_size;

	// Packets to check (to manage transmission end)
	int packets_to_check;	
	
	// To hold data
	unsigned char data[MAX_PK_DATA_SIZE];
	
	// Destination child socket
	int sock_child;
	
	// Destination address
	struct sockaddr_in cli_addr;
	
	// Is the last packet?
	int last_one;
	
	// Error condition
	int error_packet;
	
	// To send command
	int command_packet;
	
	// Numero di rispedizioni
	int resended;
	
} PACKET;

