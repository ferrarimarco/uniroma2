#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void stampa_stringa(char *string){

	if (fputs(string, stdout) == EOF)   {  //stampa string su stdout
      fprintf(stderr, "errore in fputs");
      exit(1);
    }

}