#include <stdlib.h>
#include <dirent.h>
#include <stdio.h>
#include "constants.h"
#include <unistd.h>


size_t scrivi_file(FILE *file, unsigned char *buff, int data_amount);

void lista_file(FILE *file){

	DIR *dir;
	struct dirent *ent;
	dir = opendir(SERVER_SHARE_PATH);
	unsigned int filename_length = 0;
	unsigned char *temp;

	if (dir != NULL){
		
		// print all the files and directories within directory
		while ((ent = readdir(dir)) != NULL){
			
			temp = ent->d_name;
			filename_length = strlen(temp);
			
			if(temp[0] != '.'){
				// Scrivo a capo
				scrivi_file(file, "\n\0", 1);
				
				// Scrivo dati
				scrivi_file(file, temp, filename_length);
			}
		}
		
		closedir(dir);
		
	} else {
		/* could not open directory */
		perror("file_io - lista_file - Errore opendir");
		exit(-1);
	}
}

int file_size(char *path){
	
	FILE *file;
	long position;
	int num_pacchetti;

	errno = 0;
	
	file = fopen(path, "rb");

	if( file == NULL ){
		printf( "file_size - Error opening file: %s\n", strerror( errno ) );
		exit(1);
	}

	// Calcolo le dimensioni del file
	fseek(file, 0, SEEK_END);
	position = ftell(file);
	
	// Calcolo quanti pacchetti sono necessari
	num_pacchetti = (int) position / (MAX_PK_DATA_SIZE);
	
	if(position % (MAX_PK_DATA_SIZE) > 0){
		num_pacchetti++;
	}
	
	// Reimposto la posizione all'inizio del file
	fseek(file, 0, SEEK_SET);
	
	// Chiudo file
	fclose(file);
	
	return num_pacchetti;
}

// NOTA: char ** è un puntatore ad un char *
// Necessario per modificare il valore di buff
size_t leggi_file(FILE *file, unsigned char **buff){
	
	*buff = (char *) malloc(MAX_PK_DATA_SIZE);
	size_t i = fread(*buff, sizeof(char), MAX_PK_DATA_SIZE, file);
	
	return i;
}

size_t scrivi_file(FILE *file, unsigned char *buff, int data_amount){
	
	size_t i = fwrite(buff, sizeof(char), data_amount, file);
	
	return i;
}