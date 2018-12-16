#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include "Network-Cli.h"

#define CRLF "\r\n"

int PORT = 25;
int SIZEBUF = 2000;
int soc;


void main()
{
	char snmpserver[100], dest[100], me[100], message[1000], subject[100];
	char buffer[4096];
	
	printf("Serveur SMTP à joindre:\n");
	scanf("%s", snmpserver);
	
	
	printf("Votre mail:\n");
	scanf("%s", me);
	
	printf("Destinataire:\n");
	scanf("%s", dest);
	
	printf("Subject:\n");
	scanf("%s", subject);
	
	printf("Votre message:\n");
	scanf("%s", message);
	
	
	if(Sockette(&soc, PORT, snmpserver) == -1)
        {
            perror("Err. Sockette");
            exit(-1);
            
        }
        
        Receiving(&soc, buffer, SIZEBUF);
        
        printf("%s\n", buffer);
        
        
        sprintf(buffer, "EHLO %s%s", snmpserver, CRLF);
	
	if(Sending(&soc, buffer, strlen(buffer)) == -1)
        {
            perror("Err. Sending");
            exit(-1);
        }
        
        Receiving(&soc, buffer, SIZEBUF);
        
        printf("%s\n", buffer);
        
        sprintf(buffer, "MAIL FROM:%s%s", me, CRLF);
	
	if(Sending(&soc, buffer, strlen(buffer)) == -1)
        {
            perror("Err. Sending");
            exit(-1);
        }
        
        Receiving(&soc, buffer, SIZEBUF);
        
        printf("%s\n", buffer);
        
        sprintf(buffer, "RCPT TO:%s%s", dest, CRLF);
	
	if(Sending(&soc, buffer, strlen(buffer)) == -1)
        {
            perror("Err. Sending");
            exit(-1);
        }
        
        Receiving(&soc, buffer, SIZEBUF);
        
        printf("%s\n", buffer);
        
        sprintf(buffer, "DATA%s", CRLF);
	
	if(Sending(&soc, buffer, strlen(buffer)) == -1)
        {
            perror("Err. Sending");
            exit(-1);
        }
        
        sprintf(buffer, "SUBJECT:%s%s", subject, CRLF);
	
	if(Sending(&soc, buffer, strlen(buffer)) == -1)
        {
            perror("Err. Sending");
            exit(-1);
        }
        
        sprintf(buffer, "%s%s", message, CRLF);
	
	if(Sending(&soc, buffer, strlen(buffer)) == -1)
        {
            perror("Err. Sending");
            exit(-1);
        }
        
        sprintf(buffer, ".%s", CRLF);
	
	if(Sending(&soc, buffer, strlen(buffer)) == -1)
        {
            perror("Err. Sending");
            exit(-1);
        }
        
        sprintf(buffer, "QUIT%s", CRLF);
	
	if(Sending(&soc, buffer, strlen(buffer)) == -1)
        {
            perror("Err. Sending");
            exit(-1);
        }
	
        printf("MAIL ENVOYE !\n");
        
        if(soc)
        	close(soc);
	
	
}
