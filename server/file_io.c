#include <stdlib.h>
#include <dirent.h>
#include <stdio.h>
#include "constants.h"
#include <unistd.h>



int lista_file(char *lista){

	DIR *dir;
	struct dirent *ent;
	dir = opendir(SERVER_SHARE_PATH);
	int insert_position;
	int i;
	char *temp;
	insert_position = 0;

	if (dir != NULL) {

		/* print all the files and directories within directory */
		while ((ent = readdir(dir)) != NULL) {

			temp = ent->d_name;

			//Inserisco caratteri nell'array
			for(i = 0; i < strlen(temp); i++){
				lista[i + insert_position] = temp[i];
			}

			//Aggiorno posizione
			insert_position += strlen(temp);

			//inserisco a capo
			lista[insert_position] = '\n';

			//Aggiorno posizione
			insert_position++;
		}

		//Inserisco carattere di terminazione ed aggiorno posizione
		lista[insert_position] = 0;
		insert_position++;

		closedir(dir);

		return strlen(lista);

	} else {
		/* could not open directory */
		perror("errore opendir");
		exit(-1);
	}
}


int file_size(char *path){
	
	FILE *file;
	long position;
	int num_pacchetti;

	file = fopen(path, "rb");

	if( file == NULL ){
		puts("cannot open file");
		exit(1);
	}

	// Calcolo le dimensioni del file
	fseek(file, 0, SEEK_END);
	position = ftell(file);
	
	// Calcolo quanti pacchetti sono necessari
	num_pacchetti = (int) position / (MAX_PK_DATA_SIZE - 1);
	
	if(position % (MAX_PK_DATA_SIZE - 1) > 0){
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

size_t scrivi_file(FILE *file, unsigned char *buff){
	
	size_t i = fwrite(buff, sizeof(char), MAX_PK_DATA_SIZE, file);
	
	return i;
}