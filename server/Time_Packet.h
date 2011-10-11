#include <time.h>

typedef struct {
	
	int seq_number;
	
	// 0 = not acked, 1 = acked
	int acked;
	
	// timeout
	time_t timeout;
	
} TIME_PACKET;
