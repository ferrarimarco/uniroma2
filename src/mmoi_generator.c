/*
 ============================================================================
 Name        : mmoi_generator.c
 Author      : Marco Ferrari
 Version     :
 Copyright   : http://www.ferrarimarco.info
 Description : Hello World in C, Ansi-style
 ============================================================================
 */

#include <stdio.h>
#include <stdlib.h>

#include "serial_tester.h"
#include "chi_squared_tester.h"

/* Period parameters */
#define N 624
#define M 397
#define MATRIX_A 0x9908b0dfUL   /* constant vector a */
#define UPPER_MASK 0x80000000UL /* most significant w-r bits */
#define LOWER_MASK 0x7fffffffUL /* least significant r bits */

static unsigned long mt[N]; /* the array for the state vector  */
static int mti=N+1; /* mti==N+1 means mt[N] is not initialized */

/* initializes mt[N] with a seed */
void init_genrand(unsigned long s)
{
	mt[0]= s & 0xffffffffUL;
	for (mti=1; mti<N; mti++) {
		mt[mti] =
				(1812433253UL * (mt[mti-1] ^ (mt[mti-1] >> 30)) + mti);
		/* See Knuth TAOCP Vol2. 3rd Ed. P.106 for multiplier. */
		/* In the previous versions, MSBs of the seed affect   */
		/* only MSBs of the array mt[].                        */
		/* 2002/01/09 modified by Makoto Matsumoto             */
		mt[mti] &= 0xffffffffUL;
		/* for >32 bit machines */
	}
}

/* generates a random number on [0,0xffffffff]-interval */
unsigned long genrand_int32(void)
{
	unsigned long y;
	static unsigned long mag01[2]={0x0UL, MATRIX_A};
	/* mag01[x] = x * MATRIX_A  for x=0,1 */

	if (mti >= N) { /* generate N words at one time */
		int kk;

		if (mti == N+1)   /* if init_genrand() has not been called, */
			init_genrand(5489UL); /* a default initial seed is used */

		for (kk=0;kk<N-M;kk++) {
			y = (mt[kk]&UPPER_MASK)|(mt[kk+1]&LOWER_MASK);
			mt[kk] = mt[kk+M] ^ (y >> 1) ^ mag01[y & 0x1UL];
		}
		for (;kk<N-1;kk++) {
			y = (mt[kk]&UPPER_MASK)|(mt[kk+1]&LOWER_MASK);
			mt[kk] = mt[kk+(M-N)] ^ (y >> 1) ^ mag01[y & 0x1UL];
		}
		y = (mt[N-1]&UPPER_MASK)|(mt[0]&LOWER_MASK);
		mt[N-1] = mt[M-1] ^ (y >> 1) ^ mag01[y & 0x1UL];

		mti = 0;
	}

	y = mt[mti++];

	/* Tempering */
	y ^= (y >> 11);
	y ^= (y << 7) & 0x9d2c5680UL;
	y ^= (y << 15) & 0xefc60000UL;
	y ^= (y >> 18);

	return y;
}

/* generates a random number on [0,1)-real-interval */
double genrand_real2(void)
{
	return genrand_int32()*(1.0/4294967296.0);
	/* divided by 2^32 */
}

int main(void)
{
	unsigned long init[3]={0x123, 0x234, 0x345};
	int count = 131072;
	int sub_intervals = 4096;
	double confidence = 0.10;

	int j;
	for(j = 0; j < 3; j++){

		init_genrand(init[j]);

		double *numbers;
		numbers = (double*) malloc(count * sizeof(double));

		int i;

		for (i=0; i<count; i++) {
			numbers[i] = genrand_real2();
		}

		int serial_test_result = serial_test(numbers, count, sub_intervals, confidence);
		int chi_squared_test_result = chi_squared_test(numbers, count, sub_intervals, confidence);

		char results_file_name[1024];
		snprintf(results_file_name, sizeof(results_file_name), "%u.txt", j);

		char sequence_file_name[1024];
		snprintf(sequence_file_name, sizeof(sequence_file_name), "%u_sequence.txt", j);

		char transformation_pareto_file_name[1024];
		snprintf(transformation_pareto_file_name, sizeof(transformation_pareto_file_name), "%u_pareto.txt", j);

		char transformation_weibull_file_name[1024];
		snprintf(transformation_weibull_file_name, sizeof(transformation_weibull_file_name), "%u_weibull.txt", j);

		FILE * fp_results = fopen (results_file_name,"w");
		FILE * fp_sequence = fopen (sequence_file_name,"w");
		FILE * fp_pareto = fopen (transformation_pareto_file_name,"w");
		FILE * fp_weibull = fopen (transformation_weibull_file_name,"w");

		fprintf(fp_results, "Seed: %lu\n", init[j]);
		fprintf(fp_results, "Sequence Length: %d\n", count);
		fprintf(fp_results, "Confidence: %f\n", confidence);
		fprintf(fp_results, "Sub intervals: %d\n", sub_intervals);
		fprintf(fp_results, "Test Result (serial test) (0 = PASSED, 1 = FAILED): %d\n", serial_test_result);
		fprintf(fp_results, "Test Result (chi squared test) (0 = PASSED, 1 = FAILED): %d\n", chi_squared_test_result);

		fclose(fp_results);

		for (i=0; i<count; i++) {
			fprintf(fp_sequence, "%f\n", numbers[i]);
		}

		fclose(fp_sequence);
		fclose(fp_pareto);
		fclose(fp_weibull);

		free(numbers);
	}

	return 0;
}
