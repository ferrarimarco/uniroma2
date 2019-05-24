/*
 * serial_tester.c
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

int compute_frequencies_index_serial(double x, double y, int intervals){

	double increment = 1.0 / intervals;

	double temp_index_x = x / increment;
	double temp_index_y = y / increment;

	double int_part = 0;

	modf(temp_index_x, &int_part);
	double index_x = int_part;

	modf(temp_index_y, &int_part);

	double index_y = int_part;

	int result = index_x * intervals + index_y;

	return result;
}

int serial_test(double numbers[], int number_size, int k_subintervals, double alpha){

	int p_dimensions = 2;

	int cells_number = pow(k_subintervals, p_dimensions);

	int *frequencies;
	frequencies = (int*) malloc(cells_number * sizeof(int));

	int k;
	for(k = 0; k < cells_number; k++){
		frequencies[k] = 0;
	}

	for(k = 0; k < number_size; k = k + 2){

		double firstValue = numbers[k];
		double secondValue = numbers[k+1];

		int frequencies_index = compute_frequencies_index_serial(firstValue, secondValue, k_subintervals);
		frequencies[frequencies_index]++;
	}

	// compute V
	double v = 0.0;
	int groups = number_size / p_dimensions;
	double expected_frequency = groups / pow(k_subintervals, 2);

	for(k = 0; k < cells_number; k++){
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
