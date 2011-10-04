#include "constants.h"
#include <netinet/in.h>

typedef struct {
    int seq_number;
	unsigned char data[MAX_PK_SIZE];
	int sock_child;
	struct sockaddr_in cli_addr;
} PACKET;

