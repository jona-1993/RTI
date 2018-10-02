#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include "Network-Cli.h"

int PORT = 0;
int SIZEBUF = 0;
int soc;

int main()
{
    {
        FILE* config;
        config = fopen("Client.conf", "r+");
        fseek(config, 5, SEEK_SET);
        char port[6], sizebuf[6];
        fgets(port, 6, config);
        PORT = atoi(port);
        fseek(config, 9, SEEK_CUR);
        fgets(sizebuf, 10, config);
        SIZEBUF = atoi(sizebuf);
        fclose(config);
        #ifdef DEBUG
        printf("PORT = %d, SIZEBUF = %d\n", PORT, SIZEBUF);
        #endif
    }
    
    char buffer[SIZEBUF];
    char id[20];
    char password[20];
    int accessOK = 0;
    char bin;
    
    logout:;
    accessOK = 0;
    
    do
    {
        #ifndef DEBUG
        system("clear");
        #endif
        
        printf("***** Application - Login *****\n\n");
        printf("Login: (exit pour quitter)\n");
        scanf("%s", id);
        if(strcmp(id, "exit") == 0)
        {
            if(soc) close(soc);
            exit(1);
        }
        printf("Password:\n");
        scanf("%s", password);
        
        sprintf(buffer, "%s#%s;%s", "Login", id, password);
        
        if(Sockette(&soc, PORT) == -1)
        {
            perror("Err. Sockette");
            sleep(2);
            goto logout;
        }
        
        #ifndef REQUEST // CODE MODE SANS REQUEST
        
        if(Sending(&soc, buffer, SIZEBUF) == -1)
        {
            perror("Err. Sending");
            exit(-1);
        }
        
        if(Receiving(&soc, buffer, SIZEBUF) == -1)
        {
            perror("Err. Receiving");
            exit(-1);
        }
        
        if(strcmp(buffer, "WAIT_PLEASE") == 0)
        {
            printf("Trop de clients actuellement, réessayez plus tard !\n");
            sleep(2);
        }
        
        if(strcmp(buffer, "Login#OK") == 0)
        {
            accessOK = 1;
            printf("Accès Autorisé !\n");
            sleep(2);
        }
        else
        {
            accessOK = 0;
            printf("Login ou Password Erroné !\n");
            sleep(2);
        }
        
        #else // CODE MODE REQUEST
        
        struct MessageRequest req, reqrcv;
        strcpy(req.proto, "STRUMP");
        strcpy(req.cmd, "Login");
        sprintf(req.message, "%s;%s", id, password);
        
        if(SendingRequest(&soc, req) == -1)
        {
            perror("Err. Sending");
            exit(-1);
        }
        
        if(ReceivingRequest(&soc, &reqrcv) == -1)
        {
            perror("Err. Receiving");
            exit(-1);
        }
        
        if(strcmp(reqrcv.message, "WAIT_PLEASE") == 0)
        {
            printf("Trop de clients actuellement, réessayez plus tard !\n");
            sleep(2);
        }
        
        if(strcmp(reqrcv.message, "OK") == 0)
        {
            accessOK = 1;
            printf("Accès Autorisé !\n");
            sleep(2);
        }
        else
        {
            accessOK = 0;
            printf("Login ou Password Erroné !\n");
            sleep(2);
        }
        
        #endif
        
        
        
    }
    while(accessOK != 1);
    
    
    
    char choix;
    int end = 0;
    int numAction;
    int idAction;
    char matos[20];
    char date[20];
    char type[20];
    char marque[20];
    char prix[20];
    char listeAccess[100];
    char* reste;
    
    do
    {
        bzero(matos, sizeof(char)*20);
        bzero(date, sizeof(char)*20);
        bzero(type, sizeof(char)*20);
        bzero(marque, sizeof(char)*20);
        bzero(prix, sizeof(char)*20);
        bzero(listeAccess, sizeof(char)*100);
        bzero(buffer, sizeof(char)*SIZEBUF);
        reste = NULL;
        
        #ifndef DEBUG
        system("clear");
        #endif
        printf("***** Application - Menu Principal *****\n\n");
        printf("1. Passer une demande\n");
        printf("2. Voir les commandes disponnibles\n");
        printf("3. Suppression d'une action antérieure\n");
        printf("4. Passer commande\n");
        printf("5. Déconnexion\n");
        printf("6. Quitter\n");
        fgets(&choix, 2, stdin);
        
        switch(choix)
        {
            case '1': system("clear");
                    printf("***** Application - Demande d'action *****\n\n");
                    printf("Choisissez une action:\n1. Livraison\n2. Reparation\n3. Declassement:\n\n");
                    scanf("%d", &numAction);
                    printf("Quel type de matériel?\n");
                    scanf("%s", type);
                    printf("Quel libellé de matériel:\n");
                    scanf("%s", matos);
                    printf("Saisissez une date:\n");
                    scanf("%s", date);
                    sprintf(buffer, "%s#%d;%s;%s;%s", "HMAT", numAction, type, matos, date);
                    
                    
                    if(Sending(&soc, buffer, SIZEBUF) == -1)
                    {
                        perror("Err. Sending");
                        exit(-1);
                    }
                    
                    if(Receiving(&soc, buffer, SIZEBUF) == -1)
                    {
                        perror("Err. Receiving");
                        exit(-1);
                    }
                    
                    if(strcmp(buffer, "HMAT#NOT_FILE") == 0)
		            {
		                printf("Le fichier '%s' n'existe pas!\n", matos);
		                sleep(2);
		            }
		            else if(strcmp(buffer, "HMAT#NOT_FOUND") == 0)
		            {
		                printf("Le fichier '%s' n'existe pas!\n", matos);
		                sleep(2);
		            }
		            else
		            {
		                reste = buffer;
                        strtok_r(reste, "#", &reste);
                        
                        printf("%s\n", reste);
                        
                        printf("\nInsérer quelque chose pour continuer:\n");
                        char bob[2];
                        scanf("%s", bob);
                        
		            }
                    break;
            case '2':
                    printf("Actions Commandés:\n");
                    if(Sending(&soc, "LISTCMD#1\0", SIZEBUF) == -1)
                    {
                        perror("Err. Sending");
                        exit(-1);
                    }
                    if(Receiving(&soc, buffer, SIZEBUF) == -1)
                    {
                        perror("Err. Receiving");
                        exit(-1);
                    }
                    
                    reste = buffer;
                    strtok_r(reste, "#", &reste);
                    
                    printf("%s\n", reste);
                    {
                        printf("\nInsérer quelque chose pour continuer:\n");
                        char bob[2];
                        scanf("%s", bob);
                    }
                    break;
            case '3':
                    printf("Suppression d'une Action:\n");
                    printf("Quel est l'identifiant de l'action?\n");
                    scanf("%d", &numAction);
                    printf("Quel est le Type de matériel de l'action:\n");
                    scanf("%s", type);
                    sprintf(buffer, "%s#%d;%s", "CHMAT", numAction, type);
                    if(Sending(&soc, buffer, SIZEBUF) == -1)
                    {
                        perror("Err. Sending");
                        exit(-1);
                    }
                    
                    if(Receiving(&soc, buffer, SIZEBUF) == -1)
                    {
                        perror("Err. Receiving");
                        exit(-1);
                    }
                    
                    if(strcmp(buffer, "CHMAT#OK") == 0)
                    {
                        printf("L'action a été supprimée avec succès !\n");
                        sleep(2);
                    }
                    break;
            case '4': printf("Passer Commande:\n");
                    printf("Quel est le type?\n");
                    scanf("%s", type);
                    printf("Quel est le libellé?\n");
                    scanf("%s", matos);
                    printf("Quel est la marque?\n");
                    scanf("%s", marque);
                    printf("Quel est le prix?\n");
                    scanf("%s", prix);
                    printf("Quels sont les accessoires?\n");
                    scanf("%s", listeAccess);
                    sprintf(buffer, "%s#%s;%s;%s;%s;%s", "ASKMAT", type, matos, marque, prix, listeAccess);
                    
                    if(Sending(&soc, buffer, SIZEBUF) == -1)
                    {
                        perror("Err. Sending");
                        exit(-1);
                    }
                    
                    if(Receiving(&soc, buffer, SIZEBUF) == -1)
                    {
                        perror("Err. Receiving");
                        exit(-1);
                    }
                    
                    if(strcmp("ASKMAT#ERROR_WRITE", buffer) == 0)
                    {
                        perror("Une erreur est survenue. -> Le fichier n'a pas été écrit correctement !");
                        sleep(2);
                    }
                    else if(strcmp("ASKMAT#ERROR_FILE", buffer) == 0)
                    {
                        perror("Une erreur est survenue. -> Le fichier n'a pas pu être créé/ouvert !");
                        sleep(2);
                    }
                    else
                    {
                        reste = buffer;
                        strtok_r(reste, "#", &reste);
                        
                        printf("Commandé avec succès! L'ID est: %s.\n", reste);
                        {
                            printf("\nInsérer quelque chose pour continuer:\n");
                            char bob[2];
                            scanf("%s", bob);
                        }
                    }
                    break;
            case '6': end = 1;
            case '5': printf("Logout...\n");
                      strcpy(buffer, "Logout#1");
                      if(Sending(&soc, buffer, SIZEBUF) == -1)
                      {
                          perror("Err. Sending");
                          exit(-1);
                      }
                      close(soc);
                      if(end != 1)
                        goto logout;
                      accessOK = 0;
                      break;
            default: printf("Choix Erroné: %c\n", choix);
        }
        
    }
    while(end != 1);
    
    if(soc)
        close(soc);
    
    return 1;

}
