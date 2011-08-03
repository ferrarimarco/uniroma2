#include <dirent.h>
#include "constants.h"


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

