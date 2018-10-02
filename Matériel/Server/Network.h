#ifndef NETWORK_H
#define NETWORK_H

struct MessageRequest
{
    char proto[5];
    char cmd[10];
    char message[5000];
};

int Sockette(int* soc, int port, struct sockaddr_in* s_addr);
int Waiting(int* soc, struct sockaddr_in* c_addr);

int getMessageRequest(int*, struct MessageRequest*);
int SendRequest(int*, struct MessageRequest);

#endif

