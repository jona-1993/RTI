#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netdb.h>
#include "Network-Cli.h"



    
int Sockette(int* soc, int port)
{
    struct hostent* infosHost;
    struct in_addr s_addr;
    struct sockaddr_in c_addr;
    unsigned int taillesoc;
    
    if(((*soc) = socket(AF_INET, SOCK_STREAM, 0)) == -1)
    {
        return -1;
    }
    
    if((infosHost = gethostbyname("Chocotoff")) == 0)
    {
        printf("Err d'acquisition sur l'ordi distant\n");
        return -1;
    }
    
    bzero((char*)&c_addr, sizeof(c_addr));
    c_addr.sin_family = AF_INET;
    c_addr.sin_port = htons(port);
    memcpy(&c_addr.sin_addr, infosHost->h_addr, infosHost->h_length);
    
    
    if(connect(*soc, (struct sockaddr*)&c_addr, sizeof(struct sockaddr_in)) == -1)
    {
        return -1;
    }
    
    #ifdef DEBUG
    printf("SOCKETTE SUCCESS !\n");
    #endif
    
    return 1;
    
    
}

int Sending(int* soc, char* buffer, int sizebuf)
{
    
    if(send(*soc, buffer, sizebuf, 0) == -1)
    {
        return -1;
    }
    
    
    return 1;
}



int Receiving(int* soc, char* buffer, int sizebuf)
{
    if(recv(*soc, buffer, sizebuf, 0) == -1)
    {
        return -1;
    }
    
    
    return 1;
}

int SendingRequest(int* soc, struct MessageRequest req)
{
    if(send(*soc, &req, sizeof(struct MessageRequest), 0) == -1)
    {
        return -1;
    }
    
    
    return 1;
}

int ReceivingRequest(int* soc, struct MessageRequest* req)
{
    if(recv(*soc, req, sizeof(struct MessageRequest), 0) == -1)
    {
        return -1;
    }
    
    return 1;
}
