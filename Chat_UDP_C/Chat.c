#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <pthread.h>

#define CRLF "\r\n"

int PORT = 25;
int SIZEBUF = 2000;
int soc;

int soc;
unsigned int tailleSock;
struct hostent *infoshost, *infosother;
struct in_addr adresseIP, adresseIPOther;
struct sockaddr_in addrServer, addrCli;
int sequence = 1;

pthread_t thread;


void* Thread_Receiver(void* arg)
{
	char buffer[SIZEBUF];
	int recu;
	char cmd[25];
	char uname[25];
	char msg[1000];
	int i;
	
	do
	{
		
		memset(buffer, 0, SIZEBUF);
		
		recu = recvfrom(soc, buffer, SIZEBUF, 0, &addrCli, &tailleSock);
		
		buffer[recu] = '\0';
		
		//printf("%s\n", buffer);
		for(i = 0; i < recu; i+=1)
		{
			
			if(buffer[i] == '#')
				buffer[i] = '\0';
		}
		
		
		strcpy(cmd, buffer);
		if(strcmp(cmd, "POST_EVENT") == 0)
		{
			strcpy(uname, &buffer[strlen(cmd) + 1]); // UNAME
		
			strcpy(msg, &buffer[strlen(cmd) + strlen(uname) + 2]); // Message
		
			printf("\r%s > %s\n", uname, msg);
		}
		else if(strcmp(cmd, "POST_QUESTION") == 0)
		{
			
			char size[10];
			strcpy(size, &buffer[strlen(cmd) + 1]); // Size DIG
			
			char dig[100];
			strcpy(dig, &buffer[strlen(cmd) + strlen(size) + 2]); // Digest
		
			char to[10];
			
			strcpy(to, &buffer[strlen(cmd) + strlen(size) + strlen(dig) + 3]); // SEQ
			
			
			strcpy(uname, &buffer[strlen(cmd) + strlen(size) + strlen(dig) + strlen(to) + 4]); // UNAME
			
			strcpy(msg, &buffer[strlen(cmd) + strlen(size) + strlen(dig) + strlen(to) + strlen(uname) + 5]); // Message
		
			sequence += 1;
			
			printf("\r%s > (%s)[?:%d]%s\n", uname, to, sequence, msg);
		}
		else if(strcmp(cmd, "ANSWER_QUESTION") == 0)
		{
			
			strcpy(uname, &buffer[strlen(cmd) + 1]); // UNAME
		
			char seq[10];
			
			strcpy(seq, &buffer[strlen(cmd) + strlen(uname) + 2]); // SEQ
			
			sequence = atoi(seq);
			
			strcpy(msg, &buffer[strlen(cmd) + strlen(uname) + strlen(seq) + 3]); // UNAME
			
			
		
			printf("\r%s > (Reponse)[Q:%s]%s\n", uname, seq, msg);
		}
	
		printf("Admin > ");
	}
	while(1);
	
}

void main()
{
	char sendbuf[SIZEBUF];
	char msg[SIZEBUF];
	struct ip_mreq mreq;
	
	if((soc = socket(AF_INET, SOCK_DGRAM, 0)) == -1)
	{
		perror("Err Socket");
		exit(0);
	}
	
	
	infoshost = gethostbyname("10.59.22.79");
	
	memcpy(&adresseIP, infoshost->h_addr, infoshost->h_length);
	
	
	
	tailleSock = sizeof(struct sockaddr_in);
	
	
	memset(&addrServer, 0, tailleSock);
	
	addrServer.sin_family = AF_INET;
	
	addrServer.sin_port = htons(6666);
	
	addrServer.sin_addr.s_addr = inet_addr("224.8.8.8");
	
	
	
	if(bind(soc, (struct sockaddr*)&addrServer, tailleSock) == -1)
	{
		perror("Err Bind");
		exit(0);
	}
	
	memcpy(&mreq.imr_multiaddr, &addrServer.sin_addr, tailleSock);
	
	
	mreq.imr_interface.s_addr = htons(INADDR_ANY);
	
	setsockopt(soc, IPPROTO_IP, IP_ADD_MEMBERSHIP, &mreq, sizeof(mreq));
	
	pthread_create(&thread, NULL, Thread_Receiver, NULL);
	
	
	printf("Vous êtes connecté sur le chat. Vous pouvez commencer à chatter\n");
	
	printf("Admin > ");
	
	sprintf(sendbuf, "POST_EVENT#Admin#Connected !\n");
		
	sendto(soc, sendbuf, strlen(sendbuf), 0 ,&addrServer, sizeof(struct sockaddr_in));
	
	do
	{
		
		fgets(msg, SIZEBUF, stdin);
		
		sprintf(sendbuf, "POST_EVENT#Admin#%s\n", msg);
		
		sendto(soc, sendbuf, strlen(sendbuf), 0 ,&addrServer, sizeof(struct sockaddr_in));
	}
	while(strcmp(sendbuf, "exit") != 0);
	
	
        if(soc)
        	close(soc);
	
	
}


