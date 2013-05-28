/*
 * density_analyzer.c
 *
 *  Created on: 28/mag/2013
 *      Author: Marco Ferrari <ferrari.marco@gmail.com>
 *      Website: http://www.ferrarimarco.info
 */

#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#include "density_analyzer.h"

void analyze_density(char *file_name, double *numbers, int numbers_size){

	FILE * fp_density = fopen (file_name,"w");

	double density_interval_length = 0.01;

	double maximum = 0.0;

	int i;

	for(i = 0; i < numbers_size; i++){
		if(numbers[i] > maximum){
			maximum = numbers[i];
		}
	}

	double temp_density_intervals_number = maximum / density_interval_length;

	double int_part = 0;
	double frac_part = modf(temp_density_intervals_number, &int_part);

	int density_intervals_number = int_part;

	if(frac_part > 0){
		density_intervals_number++;
	}

	double* intervals;
	intervals = (double*) malloc(density_intervals_number * sizeof(double));

	intervals[0] = 0.0;

	for(i = 1; i < density_intervals_number; i++){
		intervals[i] = intervals[i-1] + density_interval_length;
	}

	int* density;
	density = (int*) malloc(density_intervals_number * sizeof(int));

	for(i = 0; i < density_intervals_number; i++){
		density[i] = 0;
	}

	for(i = 0; i < numbers_size; i++){
		double number = numbers[i];

		int y;

		for(y = 0; y < density_intervals_number - 1; y++){

			double current_interval_start = intervals[y];
			double current_interval_end = intervals[y+1];

			if(number >= current_interval_start && number < current_interval_end){
				density[y]++;
				break;
			}
		}
	}

	for (i=0; i<density_intervals_number; i++) {
		fprintf(fp_density, "%d\n", density[i]);
	}

	fclose(fp_density);

	free(intervals);
	free(density);
}
