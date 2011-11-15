#include <time.h>

typedef struct {
	
	int seq_number;
	
	// To hold the packet to send
	PACKET packet;
	
	// Numero di rispedizioni per il pacchetto
	int total_resend;
	
	// Per gestire timeout
	struct timeval send_time;
	
} TIME_PACKET;
