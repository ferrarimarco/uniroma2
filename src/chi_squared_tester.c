/*
 * chi_squared_tester.c
 *
 *  Created on: 22/mag/2013
 *      Author: Marco Ferrari <ferrari.marco@gmail.com>
 *      Website: http://www.ferrarimarco.info
 */


#include <math.h>
#include <stdlib.h>
#include <stdio.h>
#include <stddef.h>

#include "serial_tester.h"
#include "chisq.h"
#include "v_term_calculator.h"

int compute_frequencies_index_chi_squared(double value, int intervals){

	double increment = 1.0 / intervals;

	double temp_index_x = value / increment;

	double int_part = 0;

	modf(temp_index_x, &int_part);
	double index_x = int_part;

	int result = index_x;

	return result;
}

int chi_squared_test(double numbers[], int number_size, int k_subintervals, double alpha){

	int *frequencies;
	frequencies = (int*) malloc(k_subintervals * sizeof(int));

	int k;
	for(k = 0; k < k_subintervals; k++){
		frequencies[k] = 0;
	}

	for(k = 0; k < number_size; k++){

		double value = numbers[k];

		int frequencies_index = compute_frequencies_index_chi_squared(value, k_subintervals);
		frequencies[frequencies_index]++;
	}

	// compute V
	double v = 0.0;
	double expected_frequency = number_size / k_subintervals;

	for(k = 0; k < k_subintervals; k++){
		int frequency = frequencies[k];
		v = v + compute_V_term(frequency, expected_frequency);
	}

	// Compute the critical value
	int degrees = pow(k_subintervals, 2) - 1;
	double critical_value = critchi(alpha, degrees);

	// 0 = OK
	// 1 = not OK
	int ret = 0;
	if(v > critical_value){
		ret = 1;
	}

	free(frequencies);

	return ret;
}

