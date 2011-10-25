#include <time.h>

typedef struct {
	
	int seq_number;
	
	// timeout
	time_t timeout;
	
	// To hold the packet to send
	PACKET packet;
	
} TIME_PACKET;
