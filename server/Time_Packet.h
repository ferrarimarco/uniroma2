#include <time.h>

typedef struct {
	
	int seq_number;
	
	int acked;
	
	// timeout
	time_t timeout;
	
} TIME_PACKET;
