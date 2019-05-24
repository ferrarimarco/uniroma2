/*
 * v_term_calculator.c
 *
 *  Created on: 22/mag/2013
 *      Author: Marco Ferrari <ferrari.marco@gmail.com>
 *      Website: http://www.ferrarimarco.info
 */

#include <math.h>
#include <stdlib.h>

#include "v_term_calculator.h"

double compute_V_term(double frequency, double expected_frequency){

	double num_arg = frequency - expected_frequency;
	double num = pow(num_arg, 2);

	double result = num / expected_frequency;

	return result;
}
