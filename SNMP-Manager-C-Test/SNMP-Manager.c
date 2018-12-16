#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netdb.h>

#define CRLF "\r\n"

int PORT = 25;
int SIZEBUF = 2000;
int soc;


char peer0_0[] = { /* Packet 67 */
0x30, 0x2c, 0x02, 0x01, 0x00, 0x04, 0x09, 0x63, 
0x6f, 0x6d, 0x6d, 0x75, 0x6e, 0x69, 0x74, 0x79, 
0xa0, 0x1c, 0x02, 0x04, 0x28, 0xa1, 0x47, 0xa6, 
0x02, 0x01, 0x00, 0x02, 0x01, 0x00, 0x30, 0x0e, 
0x30, 0x0c, 0x06, 0x08, 0x2b, 0x06, 0x01, 0x02, 
0x01, 0x01, 0x05, 0x00, 0x05, 0x00 }; // GET_REQUEST DU NOM DE LA MACHINE (COMMUNAUTE = community)



void main()
{
	int soc;
	struct hostent *infoshost, *infosother;
	struct in_addr adresseIP, adresseIPOther;
	struct sockaddr_in addrServer, addrCli;
	unsigned int tailleSock;
	
	char buffer[SIZEBUF];

	
	
	soc = socket(AF_INET, SOCK_DGRAM, 0);
	
	
	infoshost = gethostbyname("192.168.10.1");
	
	memcpy(&adresseIP, infoshost->h_addr, infoshost->h_length);
	
	tailleSock = sizeof(struct sockaddr_in);
	
	memset(&addrCli, 0, sizeof(struct sockaddr_in));
	
	addrCli.sin_family = AF_INET;
	
	addrCli.sin_port = htons(52405); // client port
	
	memcpy(&addrCli.sin_addr, infoshost->h_addr, infoshost->h_length);
	
	bind(soc, (struct sockaddr*)&addrCli, tailleSock);
	
	
	
	
	infosother = gethostbyname("192.168.10.100");
	
	memcpy(&adresseIPOther, infosother->h_addr, infosother->h_length);
	
	memset(&addrServer, 0, sizeof(struct sockaddr_in));
	
	addrServer.sin_family = AF_INET;
	
	addrServer.sin_port = htons(161); // port snmp
	
	memcpy(&addrServer.sin_addr, infosother->h_addr, infosother->h_length);
	
	sendto(soc, peer0_0, sizeof(peer0_0), 0 ,&addrServer, sizeof(struct sockaddr_in));
	
	int recu;
	
	printf("Reçu: %d\n\n", (recu = recvfrom(soc, buffer, SIZEBUF, 0, &addrServer, &tailleSock)));
        
        for(int i = 0; i < recu; i+=1)
        {
        	if(buffer[i] == '\0')
        	{
        		buffer[i] = '\t';
        	}
        }
        
        
        buffer[recu] = '\0';
        
        
        printf("RETOUR BRUT = %s\n\n", buffer);
        
        strcpy(buffer, (buffer + 46));
        
        printf("RETOUR FILTRE = %s\n", buffer);
        
        if(soc)
        	close(soc);
	
	
}
