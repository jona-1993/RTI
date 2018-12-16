#ifndef NETWORK_CLI_H
#define NETWORK_CLI_H

struct MessageRequest
{
    char proto[5];
    char cmd[10];
    char message[5000];
};

int Sockette(int* soc, int port, char* ip);
int Sending(int* soc, char* buffer, int sizebuf);
int Receiving(int* soc, char* buffer, int sizebuf);

int SendingRequest(int*, struct MessageRequest);
int ReceivingRequest(int*, struct MessageRequest*);

#endif
