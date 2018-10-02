#include <fcntl.h> 
#include <stdio.h> 
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <pthread.h>
#include <dirent.h>

#include "Libcsv.h"
#include "Network.h"


int soc;
int PORT = 0;
int NBCLI = 0;
int* connexions;
int Stop = 1;

pthread_mutex_t MutexNewUser;
pthread_mutex_t mutexIndiceCourant;
pthread_cond_t condIndiceCourant;
int indiceCourant = -1;

void* TraitementClient(void* arg)
{
    #ifdef DEBUG
    printf("Thread de traitements lancé !\n");
    #endif
    struct sockaddr_in c_addr;
    char user[20];
	char buffer[6000];
	int conn_desc = 0;
	int iCliTraite = 0;
	int finDialogue = 0;
	char delem = '#';
	char* saveptr;
	int i;
	char bin[2];
	int ret = 0;
	
	while(Stop != 1)
	{
	    newConnect:;
	    pthread_mutex_lock(&mutexIndiceCourant);
	    while(indiceCourant == -1)
	        pthread_cond_wait(&condIndiceCourant, &mutexIndiceCourant);
	    iCliTraite = indiceCourant;
	    indiceCourant = -1;
	    conn_desc = connexions[iCliTraite];
	    pthread_mutex_unlock(&mutexIndiceCourant);
	    // Accepté par le thread de service
	    
	    finDialogue = 0;
	    do
	    {
	        #ifndef REQUEST // CODE MODE SANS REQUEST
	        if((ret = recv(conn_desc, buffer, 50, 0)) < 1)
		    {
		        if(ret == 0) // Pipe TCP perdu
		        {
		            close(conn_desc);
		            pthread_mutex_lock(&mutexIndiceCourant);
		            connexions[i] = -1;
		            pthread_mutex_unlock(&mutexIndiceCourant);
		            printf("%s s'est déconnecté !\n", user);
		            goto newConnect;
		        }
		        else
		        {
			        perror("Err. recv");
			        printf("%s s'est déconnecté !\n", user);
			        goto end_connexion;
			    }
		    }
		    
		    strtok_r(buffer, &delem, &saveptr);
		    
		    
		    if(strcmp(buffer, "Login") == 0)
		    {
		        pthread_setcancelstate(PTHREAD_CANCEL_DISABLE, NULL);
			    #ifdef DEBUG
			    printf("Requête Login!\n");
			    #endif
			    char login[100][25], password[100][25];
			    int i = 0;
			    int fd;
			    char delem2 = ';';
			    char* saveptr2;
			
			    strtok_r(saveptr, &delem2, &saveptr2);
			
			    pthread_mutex_lock(&MutexNewUser);
			
			    fd = open("Saves/Login.csv", O_RDONLY);
			    lseek(fd, 0, SEEK_SET);
			
			    #ifdef DEBUG
			    printf("Recherche: %s et %s\n", saveptr, saveptr2);
			    #endif
			    for(i = 0; ReadByte(fd, &login[i][0], &password[i][0]); i+=1)
			    {
				
				    if(strcmp(&login[i][0], saveptr) == 0)
				    {   
					    if(strcmp(&password[i][0], saveptr2) == 0)
					    {
						    //OK
						    printf("%s s'est connecté!\n", saveptr);
						    strcpy(user, saveptr);
						    send(conn_desc, "Login#OK", 50, 0);
						    goto end;
					    }
					
				    }
			    }
			    #ifdef DEBUG
			    printf("Pas trouvé !\n");
			    #endif
			    send(conn_desc, "Login#NOK", 50, 0);
			    end:;
			
			    close(fd);
			
			    pthread_mutex_unlock(&MutexNewUser);
			    
			    pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, NULL);
		    }
		    
		    if(strcmp(buffer, "HMAT") == 0)
		    {
		        pthread_setcancelstate(PTHREAD_CANCEL_DISABLE, NULL);
		        char* token;
		        char* reste = saveptr;
		        int fd;
		        char FileName[50];
		        char Matos[20];
		        char Action[20];
		        char Date[20];
		        char Type[20];
		        
		        while(token = strtok_r(reste, ";", &reste))
		        {
		            printf("%s\n", token);
		            
		            switch(i)
		            {
		                case 0: 
		                        switch(token[0])
		                        {
		                            case '1':
		                                    strcpy(Action, "Livé");
		                                    break;
		                            case '2':
		                                    strcpy(Action, "Réparé");
		                                    break;
		                            default: 
		                                    strcpy(Action, "Déclassé");
		                        }
		                        break;
		                case 1:
		                       strcpy(Type, token);
		                       sprintf(FileName, "Saves/%s.csv", Type);
		                       break;
		                case 2:
		                       strcpy(Matos, token);
		                       break;
		                case 3: 
		                        strcpy(Date, token);
		                        break;
		            }
		                
		            i+=1;
		        }
		        
		        if((fd = open(FileName, O_RDONLY)) == -1)
		        {
		            send(conn_desc, "HMAT#NOT_FILE", 14, 0);
		        }
		        else
		        {
		            char id[20];
		            char Name[20];
		            char r3[20];
		            char r4[20];
		            char r5[200];
		            int ret = 0;
		            
		            do
		            {
		                ret = ReadByte5(fd, id, Name, r3, r4, r5);
		            }
		            while(strcmp(Matos, Name) != 0 || ret != 1);
		            
		            close(fd);
		            
		            if((fd = open("Saves/Actions.csv", O_CREAT | O_RDWR, 0777)) == -1)
		            {
		                send(conn_desc, "HMAT#NOT_FILE", 14, 0);
		            }
		            else
		            {
		                {
		                    char bob[200];
		                    while(ReadByte5(fd, bob, bob, bob, bob, bob) == 1);
		                }
		                ret = WriteByte5(fd, id, Type, Name, Action, Date);
		                close(fd);
		            }
		            if(ret == 0)
		            {
		                send(conn_desc, "HMAT#NOT_FOUND", 20, 0);
		            }
		            else
		            {
		                
		                char buffer[100];
		                sprintf(buffer, "HMAT#Le matériel '%s' sera %s le %s !", Matos, Action, Date);
		                send(conn_desc, buffer, strlen(buffer) + 1, 0);
		            }
		            
		        }
		        pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, NULL);
		    }
		    
		    if(strcmp(buffer, "LISTCMD") == 0)
		    {
		        char FileName[50];
		        int fd;
		        DIR *d;
		        struct dirent *dir;
		        d = opendir("./Saves");
		        if(d)
		        {
		            strcpy(buffer, "LISTCMD#");
		            
		            while((dir = readdir(d)) != NULL)
		            {
		                if((strcmp(dir->d_name, "Login.csv") != 0) && dir->d_name[0] != '.')
		                {
		                    #ifdef DEBUG
		                    printf("FOUND: %s\n", dir->d_name);
		                    #endif
		                    
		                    sprintf(FileName, "Saves/%s", dir->d_name);
		                    
		                    pthread_setcancelstate(PTHREAD_CANCEL_DISABLE, NULL);
		                    
		                    fd = open(FileName, O_RDONLY); 
		                    if(fd)
		                    {
		                        char id[10];
		                        char libelle[20];
		                        char marque[20];
		                        char prix[10];
		                        char listeAccess[100];
		        
		                        
		                        while(ReadByte5(fd, id, libelle, marque, prix, listeAccess))
		                        {
		                            if(atoi(id) > 0)
		                            {
		                                strcat(buffer, "[");
		                                strcat(buffer, id);
		                                strcat(buffer, ", ");
		                                strcat(buffer, libelle);
		                                strcat(buffer, ", ");
		                                strcat(buffer, marque);
		                                strcat(buffer, ", ");
		                                strcat(buffer, prix);
		                                strcat(buffer, ", ");
		                                strcat(buffer, listeAccess);
		                                strcat(buffer, "]\n");
		                            }
		                        }
		                        close(fd);
		                        pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, NULL);
		                    }
		                    
		                }
		            }
		            #ifdef DEBUG
		            printf("SEND: %s\n", buffer);
		            #endif
		            send(conn_desc, buffer, strlen(buffer) + 1, 0);
		            
		            closedir(d);
		        }
		    }
		    
		    if(strcmp(buffer, "ASKMAT") == 0)
		    {
		        pthread_setcancelstate(PTHREAD_CANCEL_DISABLE, NULL);
		        
		        char* token;
		        char* reste = saveptr;
		        int fd;
		        char FileName[50];
		        char id[10];
		        char type[20];
                char libelle[20];
                char marque[20];
                char prix[10];
                char listeAccess[100];
		        int i = 0;
		        
		        while(token = strtok_r(reste, ";", &reste))
		        {
		            switch(i)
		            {
                        case 0: strcpy(type, token);
                                break;
                        case 1: strcpy(libelle, token);
                                break;
                        case 2: strcpy(marque, token);
                                break;
                        case 3: strcpy(prix, token);
                                break;
                        case 4: strcpy(listeAccess, token);
                                break;          
		            }
		            
		            i+=1;
		        }
		        sprintf(FileName, "Saves/%s.csv", type);
		            
	            fd = open(FileName, O_CREAT | O_RDWR, 0777); 
	            
	            if(fd)
	            {
	                int i = 0;
	                char bob[100];
	                for(i = 0; ReadByte5(fd, bob, bob, bob, bob, bob) ; i+=1); // Générer l'ID (Auto-Incr.)
	                sprintf(id, "%d", i+1);
	                
	                if(WriteByte5(fd, id, libelle, marque, prix, listeAccess))
	                {
	                    sprintf(id, "ASKMAT#%d", i+1);
	                    send(conn_desc, id, strlen(id) + 1, 0);
	                }
	                else
	                {
	                    send(conn_desc, "ASKMAT#ERROR_WRITE", 20, 0);
	                }
	                close(fd);
	            }
	            else
	            {
	                send(conn_desc, "ASKMAT#ERROR_FILE", 20, 0);
	            }
	            
	            pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, NULL);
		    }
		    
		    if(strcmp(buffer, "CHMAT") == 0)
		    {
		        pthread_setcancelstate(PTHREAD_CANCEL_DISABLE, NULL);
		        
		        int fd;
		        int ret;
		        int i = 0;
		        char id[10];
		        char type[20];
		        char idrcv[10];
		        char typercv[20];
		        char* token;
		        char* reste = saveptr;
		        
		        while(token = strtok_r(reste, ";", &reste))
		        {
		            switch(i)
		            {
                        case 0: strcpy(idrcv, token);
                                break;
                        case 1: strcpy(typercv, token);
                                break;         
		            }
		            
		            i+=1;
		        }
		        
		        if(!(fd = open("Saves/Actions.csv", O_RDWR)))
		        {
		            send(conn_desc, "CHMAT#NOK", 10, 0);
		        }
		        else
		        {
		            char bob[2];
		            int ret = 1;
		            
		            lseek(fd, 0, SEEK_SET);
		            
		            while(ReadByte5(fd, id, type, bob, bob, bob))
		            {
		                if((strcmp(id, idrcv) == 0) && (strcmp(type, typercv) == 0))
		                {
		                    PreviewLine(fd);
		                    ret &= EraseLine(fd, '0');
		                    goto exit;
		                }
		            }
		            exit:;
		            if(ret == 1)
		                send(conn_desc, "CHMAT#OK", 10, 0);
		            else
		                send(conn_desc, "CHMAT#NOK", 10, 0);
		            
		            close(fd);
		        }
		        
		        pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, NULL);
		    }
		    
		    if(strcmp(buffer, "Logout") == 0)
		    {
		        pthread_setcancelstate(PTHREAD_CANCEL_DISABLE, NULL);
		        #ifdef DEBUG
		        printf("Logout...\n");
		        #endif
		        
		        close(conn_desc);
		        
	            pthread_mutex_lock(&mutexIndiceCourant);
	            connexions[i] = -1;
	            pthread_mutex_unlock(&mutexIndiceCourant);
	            printf("%s s'est déconnecté !\n", user);
	            
	            pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, NULL);
		        
		        goto newConnect;
		    }
		    
		    #else // CODE MODE REQUEST
		    
		    struct MessageRequest req, reqrcv;
		    
		    getMessageRequest(&conn_desc, &reqrcv);
		    
		    
		    if(strcmp(reqrcv.cmd, "Login") == 0)
		    {
			    #ifdef DEBUG
			    printf("Requête Login!\n");
			    #endif
			    char login[100][25], password[100][25];
			    int i = 0;
			    int fd;
			    char delem2 = ';';
			    char loginrcv[25], passwordrcv[25];
			    char* reste = reqrcv.message;
			    char* token;
		        
		        while(token = strtok_r(reste, ";", &reste))
		        {
		            switch(i)
		            {
                        case 0: strcpy(loginrcv, token);
                                break;
                        case 1: strcpy(passwordrcv, token);
                                break;         
		            }
		            
		            i+=1;
		        }
			
			    pthread_mutex_lock(&MutexNewUser);
			
			    pthread_setcancelstate(PTHREAD_CANCEL_DISABLE, NULL);
			    
			    fd = open("Saves/Login.csv", O_RDONLY);
			    lseek(fd, 0, SEEK_SET);
			
			    #ifdef DEBUG
			    printf("Recherche: %s et %s\n", loginrcv, passwordrcv);
			    #endif
			    
			    for(i = 0; ReadByte(fd, &login[i][0], &password[i][0]); i+=1)
			    {
				
				    if(strcmp(&login[i][0], loginrcv) == 0)
				    {
					    if(strcmp(&password[i][0], passwordrcv) == 0)
					    {
						    //OK
						    printf("%s s'est connecté!\n", loginrcv);
						    strcpy(user, loginrcv);
						    strcpy(req.proto, "STRUMP");
                            strcpy(req.cmd, "Login");
                            sprintf(req.message, "OK");
                            SendRequest(&conn_desc, req);
						    goto end;
					    }
					
				    }
			    }
			    #ifdef DEBUG
			    printf("Pas trouvé !\n");
			    #endif
			    send(conn_desc, "Login#NOK", 50, 0);
			    end:;
			
			    close(fd);
			
			    pthread_mutex_unlock(&MutexNewUser);
			
			    pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, NULL);
		    }
		    
		    #endif
		    
		}
		while(Stop == 0);
	    
		end_connexion:;
		
		if(conn_desc)
		    close(conn_desc);
		    
		#ifdef DEBUG
		printf("Fin de Thread de Service\n");
		#endif
	}
}


void* Serveur(void* arg)
{
    struct sockaddr_in s_addr;
    int soc_serv;
    int j = 0;
	
	pthread_setcancelstate(PTHREAD_CANCEL_DISABLE, NULL);
	
	if(Sockette(&soc, PORT, &s_addr) == -1)
	{
		perror("Err. de bind");
		exit(-1);
	}
	
	
	pthread_t pidTraitement[NBCLI];
	
	for(int i = 0; i < NBCLI; i+=1)
		pthread_create(&pidTraitement[i], NULL, TraitementClient, NULL);
	
	pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, NULL);
	
	do
    {
    	#ifdef DEBUG
        printf("En attente d'un Client\n");
        #endif
        soc_serv = Waiting(&soc, &s_addr);
        
        if(soc_serv)
        {
            for(j = 0; j < NBCLI && connexions[j] != -1; j+=1);
            
            pthread_setcancelstate(PTHREAD_CANCEL_DISABLE, NULL);
            
            if(j == NBCLI)
            {
                #ifdef DEBUG
                printf("Plus de connexion\n");
                #endif
                
                if(send(soc_serv, "WAIT_PLEASE\0", 13, 0))
                {
                    perror("Erreur send");
                    // J'exit pas, c'est pas parce qu'il a foiré ici que je dois être radical..
                }
                close(soc_serv);
            }
            else
            {
                pthread_mutex_lock(&mutexIndiceCourant);
                connexions[j] = soc_serv;
                indiceCourant = j;
                pthread_mutex_unlock(&mutexIndiceCourant);
                pthread_cond_signal(&condIndiceCourant);
            }
            
            pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, NULL);
        }
    }
    while(Stop != 1);

	
	close(soc);
	
	printf("Le serveur a été fermé !\n");
	pthread_exit(NULL);
	return NULL;
}

int main()
{
    pthread_mutex_init(&mutexIndiceCourant, NULL);
    pthread_cond_init(&condIndiceCourant, NULL);
    pthread_mutex_init(&MutexNewUser, NULL);
    
{	// Chargement du fichier de configuration: Doit exister !
	FILE* config;

	config = fopen("Serveur.conf", "r+");
	fseek(config, 5, SEEK_SET);
	char port[6], nbcli[6];
	fgets(port, 6, config);
	PORT = atoi(port);
	fseek(config, 7, SEEK_CUR);
	fgets(nbcli, 2, config);
	NBCLI = atoi(nbcli);
	
	#ifdef DEBUG
	printf("PORT = %d\n", PORT);
	
	printf("NBCLI = %d\n", NBCLI);
	#endif
	fclose(config);
}	


	int end = 0;
	char choix = 0;
	pthread_t pidServ;
	char id[25], pass[25];
	int fd;
	
	
	do
	{
		printf("**********************************************\nMateriel - Server\
	\n**********************************************\n\n");
		printf("Menu Principal:\n\n");
		printf("1. Lancer le serveur\n");
		printf("   Administration:\n");
		printf("      2. Ajouter un user\n");
		printf("3. Fermer le serveur\n");
		printf("4. Quitter\n");
		
		
		fgets(&choix, 2, stdin);
			
		switch(choix)
		{
			case '1':
				if(Stop == 1)
				{
					Stop = 0;
					connexions = (int*)malloc(NBCLI*sizeof(int));
	                for(int i = 0; i < NBCLI; i+=1)
		                *(connexions+i) = -1;
					pthread_create(&pidServ, NULL, Serveur, NULL);
					printf("\n\n*** Serveur Démarré ! ***\n\n");
				}
				else
				{
					printf("\n\n*** Serveur déjà démarré ! ***\n\n");
				}
				break;
			case '2': printf("Identifiant:\n");
				  scanf("%s", id);
				  printf("Password:\n");
				  while((pass[0] = getchar()) != '\n' && pass[0] != EOF);
				  scanf("%s", pass);
				  pthread_mutex_lock(&MutexNewUser);
				  fd = open("Login.csv", O_CREAT | O_RDWR, "777");
				  lseek(fd, 0, SEEK_END);
			
		  		  if(WriteByte(fd, id, pass))
				  {
					  printf("\n\n*** Utilisateur enregistré ! ***\n\n");
				  }
				  else
				  {
					  perror("Err. WriteByte()");
				  }
				  close(fd);
				  pthread_mutex_unlock(&MutexNewUser);
				  
				  break;
			case '4': end = 1;
			case '3':
			      if(Stop == 0)
			      {
				      pthread_mutex_lock(&mutexIndiceCourant);
				      for(int i = 0; i < NBCLI; i+= 1)
				      {
				      	
				      	if(connexions[i] != -1)
				      	{
				      		printf("Connexion %d fermée !\n", connexions[i]);
				      		close(connexions[i]);
				      		connexions[i] = -1;
				      	}
				      	
				      }
				      close(soc);
				      Stop = 1;
				      if(connexions)
				        free(connexions);
				      pthread_mutex_unlock(&mutexIndiceCourant);
				      
			          pthread_cancel(pidServ);
			          pthread_detach(pidServ);
				      
				      printf("\n\n*** Serveur éteint ! ***\n\n");
				  }
				  else
				  {
				    printf("\n\n*** Serveur déjà éteint ! ***\n\n");
				  }
				  break;
				  
		}
		while((pass[0] = getchar()) != '\n' && pass[0] != EOF);
	
	}
	while(end != 1);

	printf("\n\n*** Fermeture.. ***\n\n");
	
	return 1;
}
