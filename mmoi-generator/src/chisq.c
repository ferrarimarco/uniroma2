/*
       Module:       chisq.c
       Purpose:      compute approximations to chisquare distribution probabilities
       Contents:     pochisq(), critchi()
       Uses:         poz() in z.c (Algorithm 209)
       Programmer:   Gary Perlman
       Organization: Wang Institute, Tyngsboro, MA 01879
       Tester:       compile with -DCHISQTEST to include main program
       Copyright:    none
       Tabstops:     4
 */

/*LINTLIBRARY*/

#include <math.h>
/*static char sccsfid[] = "@(#) chisq.c 5.2 (|stat) 12/08/86";*/

#define        CHI_EPSILON     0.000001    /* accuracy of critchi approximation */
#define        CHI_MAX     99999.0         /* maximum chi square value */

#define        LOG_SQRT_PI     0.5723649429247000870717135 /* log (sqrt (pi)) */
#define        I_SQRT_PI       0.5641895835477562869480795 /* 1 / sqrt (pi) */
#define        BIGX           20.0         /* max value to represent exp (x) */
#define        ex(x)             (((x) < -BIGX) ? 0.0 : exp (x))
#define        Z_EPSILON      0.000001       /* accuracy of critz approximation */
#define        Z_MAX          6.0            /* maximum meaningful z value */

/*FUNCTION poz: probability of normal z value */
/*ALGORITHM
       Adapted from a polynomial approximation in:
               Ibbetson D, Algorithm 209
               Collected Algorithms of the CACM 1963 p. 616
       Note:
               This routine has six digit accuracy, so it is only useful for absolute
               z values < 6.  For z values >= to 6.0, poz() returns 0.0.
 */
/*VAR returns cumulative probability from -oo to z */
//double z;        /*VAR normal z value */
double poz(double z){
	double  y, x, w;

	if (z == 0.0)
		x = 0.0;
	else
	{
		y = 0.5 * fabs (z);
		if (y >= (Z_MAX * 0.5))
			x = 1.0;
		else if (y < 1.0)
		{
			w = y*y;
			x = ((((((((0.000124818987 * w
					-0.001075204047) * w +0.005198775019) * w
					-0.019198292004) * w +0.059054035642) * w
					-0.151968751364) * w +0.319152932694) * w
					-0.531923007300) * w +0.797884560593) * y * 2.0;
		}
		else
		{
			y -= 2.0;
			x = (((((((((((((-0.000045255659 * y
					+0.000152529290) * y -0.000019538132) * y
					-0.000676904986) * y +0.001390604284) * y
					-0.000794620820) * y -0.002034254874) * y
					+0.006549791214) * y -0.010557625006) * y
					+0.011630447319) * y -0.009279453341) * y
					+0.005353579108) * y -0.002141268741) * y
					+0.000535310849) * y +0.999936657524;
		}
	}
	return (z > 0.0 ? ((x + 1.0) * 0.5) : ((1.0 - x) * 0.5));
}

/*FUNCTION critz: compute critical z value to produce given probability */
/*ALGORITHM
       Begin with upper and lower limits for z values (maxz and minz)
       set to extremes.  Choose a z value (zval) between the extremes.
       Compute the probability of the z value.  Set minz or maxz, based
       on whether the probability is less than or greater than the
       desired p.  Continue adjusting the extremes until they are
       within Z_EPSILON of each other.
 */
/*VAR returns z such that fabs (poz(p) - z) <= .000001 */
//double p;    /*VAR critical probability level */
double critz (double p){
	double  minz = -Z_MAX;    /* minimum of range of z */
	double  maxz = Z_MAX;     /* maximum of range of z */
	double  zval = 0.0;       /* computed/returned z value */
	double  poz (), pval;     /* prob (z) function, pval := poz (zval) */


	if (p <= 0.0 || p >= 1.0)
		return (0.0);

	while (maxz - minz > Z_EPSILON)
	{
		pval = poz (zval);
		if (pval > p)
			maxz = zval;
		else
			minz = zval;
		zval = (maxz + minz) * 0.5;
	}
	return (zval);
}

/*FUNCTION pochisq: probability of chi sqaure value */
/*ALGORITHM Compute probability of chi square value.
       Adapted from:
               Hill, I. D. and Pike, M. C.  Algorithm 299
               Collected Algorithms for the CACM 1967 p. 243
       Updated for rounding errors based on remark in
               ACM TOMS June 1985, page 185
 */
//double x;       /* obtained chi-square value */
//int df;      /* degrees of freedom */
double pochisq (double x, int df){
	double  a, y, s;
	double  e, c, z;
	double  poz ();   /* computes probability of normal z score */
	int     even;     /* true if df is an even number */

	if (x <= 0.0 || df < 1)
		return (1.0);

	a = 0.5 * x;
	even = (2*(df/2)) == df;
	if (df > 1)
		y = ex (-a);
	s = (even ? y : (2.0 * poz (-sqrt (x))));
	if (df > 2)

	{
		x = 0.5 * (df - 1.0);
		z = (even ? 1.0 : 0.5);
		if (a > BIGX)
		{
			e = (even ? 0.0 : LOG_SQRT_PI);
			c = log (a);
			while (z <= x)
			{
				e = log (z) + e;
				s += ex (c*z-a-e);
				z += 1.0;
			}
			return (s);
		}
		else
		{
			e = (even ? 1.0 : (I_SQRT_PI / sqrt (a)));
			c = 0.0;
			while (z <= x)
			{
				e = e * (a / z);
				c = c + e;
				z += 1.0;
			}
			return (c * y + s);
		}
	}
	else
		return (s);
}

/*FUNCTION critchi: compute critical chi square value to produce given p */
double critchi (double p, int df){
	double  minchisq = 0.0;
	double  maxchisq = CHI_MAX;
	double  chisqval;

	if (p <= 0.0)
		return (maxchisq);
	else if (p >= 1.0)
		return (0.0);

	chisqval = df / sqrt (p);    /* fair first value */
	while (maxchisq - minchisq > CHI_EPSILON)
	{
		if (pochisq (chisqval, df) < p)
			maxchisq = chisqval;
		else
			minchisq = chisqval;
		chisqval = (maxchisq + minchisq) * 0.5;
	}
	return (chisqval);
}


