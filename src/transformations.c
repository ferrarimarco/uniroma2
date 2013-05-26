/*
 * transformations.c
 *
 *  Created on: 25/mag/2013
 *      Author: Marco
 */

#include <math.h>
#include <stdlib.h>

#include "transformations.h"

double pareto(double u, double k, double alpha){

	double num = k;

	double pow_arg = 1.0 / alpha;
	double den = pow(u, pow_arg);

	double result = num / den;

	return result;
}

double weibull(double u, double a, double b){

	double first_term = a;
	double exp = 1.0 / b;
	double second_term = pow(0.0 - log(u), exp);

	double result = first_term * second_term;

	return result;
}

