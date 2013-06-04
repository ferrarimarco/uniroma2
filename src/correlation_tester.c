/*
 * correlation_tester.c
 *
 *  Created on: 04/giu/2013
 *      Author: Marco Ferrari <ferrari.marco@gmail.com>
 *      Website: http://www.ferrarimarco.info
 */


#include <stdlib.h>
#include <math.h>

#include "correlation_tester.h"

double correlation_tester(double *seq_x, double *seq_y, int seq_len){

	int i;

	double mean_x = 0.0;
	double mean_y = 0.0;

	for(i=0; i<seq_len; i++){
		mean_x += seq_x[i];
		mean_y += seq_y[i];
	}

	mean_x = mean_x / seq_len;
	mean_y = mean_y / seq_len;

	double num = 0.0;
	double den_1 = 0.0;
	double den_2 = 0.0;

	for(i=0; i<seq_len; i++){

		double xi_x = seq_x[i] - mean_x;
		double yi_y = seq_y[i] - mean_y;

		num += xi_x * yi_y;

		den_1 += pow(xi_x, 2);
		den_2 += pow(yi_y, 2);
	}

	den_1 = sqrt(den_1);
	den_2 = sqrt(den_2);

	double den = den_1 * den_2;

	double rho_xy = num / den;

	return rho_xy;
}
