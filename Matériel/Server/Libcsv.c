#include <fcntl.h> 
#include <stdio.h> 
#include <string.h>
#include <unistd.h> 
#include "Libcsv.h"


int ReadByte(int fd, char* l, char* p)
{
	char buff;
	int end;
	
	for(int i = 0; buff != ';' && buff != ','; i+=1)
	{
		end = read(fd, &buff, 1);
		
			
		if(end == 0)
		{
			goto end;
		}
		
		*(l+i) = buff;
		
		if(*(l+i) == ';' || *(l+i) == ',')
			*(l+i) = '\0';
			
	}
	
	buff = 1;
	int i = 0;
	for(i = 0; buff != '\n'; i+=1)
	{
		end = read(fd, &buff, 1);
		if(end == 0)
		{
			fprintf(stderr,"goto\n");
			goto end;
		}
		*(p+i) = buff;
	}
	*(p+i-1) = '\0';
	
	
	return 1;
	
	end:;
	
	return 0;
}

int WriteByte(int fd, char* l, char* p)
{
	char buff;
	int end = 1;

	if(write(fd, l, sizeof(l)) == 0)
		return 0;
	
	
	buff = ';';
	
	if(write(fd, &buff, 1) == 0)
		return 0;
	
	if(write(fd, p, sizeof(p)) == 0)
		return 0;
	
	buff = '\n';
	
	if(write(fd, &buff, 1) == 0)
		return 0;
	
	return 1;
	
}


int ReadByte5(int fd, char* r1, char* r2, char* r3, char* r4, char* r5)
{
	char buff;
	int end;
	
	for(int i = 0; buff != ';' && buff != ','; i+=1)
	{
		end = read(fd, &buff, 1);
		
			
		if(end == 0)
		{
			goto end;
		}
		
		*(r1+i) = buff;
		
		if(*(r1+i) == ';' || *(r1+i) == ',')
			*(r1+i) = '\0';
			
	}
	
	buff = 1;
	
	for(int i = 0; buff != ';' && buff != ','; i+=1)
	{
		end = read(fd, &buff, 1);
		
			
		if(end == 0)
		{
			goto end;
		}
		
		*(r2+i) = buff;
		
		if(*(r2+i) == ';' || *(r2+i) == ',')
			*(r2+i) = '\0';
			
	}
	
	buff = 1;
	
	for(int i = 0; buff != ';' && buff != ','; i+=1)
	{
		end = read(fd, &buff, 1);
		
			
		if(end == 0)
		{
			goto end;
		}
		
		*(r3+i) = buff;
		
		if(*(r3+i) == ';' || *(r3+i) == ',')
			*(r3+i) = '\0';
			
	}
	
	buff = 1;
	
	for(int i = 0; buff != ';' && buff != ','; i+=1)
	{
		end = read(fd, &buff, 1);
		
			
		if(end == 0)
		{
			goto end;
		}
		
		*(r4+i) = buff;
		
		if(*(r4+i) == ';' || *(r4+i) == ',')
			*(r4+i) = '\0';
			
	}
	
	buff = 1;
	
	
	int i = 0;
	
	while(buff != '\n')
	{
		end = read(fd, &buff, 1);
		
		if(end == 0)
		{
			fprintf(stderr,"goto\n");
			goto end;
		}
		*(r5+i) = buff;
		i+=1;
	}
	*(r5+i-1) = '\0';
	
	
	return 1;
	
	end:;
	
	return 0;
}

int WriteByte5(int fd, char* r1, char* r2, char* r3, char* r4, char* r5)
{
	char buff;
	int end = 1;

	
	if(write(fd, r1, sizeof(r1)) == 0)
		return 0;
	
	
	buff = ';';
	
	if(write(fd, &buff, 1) == 0)
		return 0;
	
	if(write(fd, r2, sizeof(r2)) == 0)
		return 0;
		
    if(write(fd, &buff, 1) == 0)
		return 0;
	
	if(write(fd, r3, sizeof(r3)) == 0)
		return 0;
		
    if(write(fd, &buff, 1) == 0)
		return 0;
	
	if(write(fd, r4, sizeof(r4)) == 0)
		return 0;
		
	if(write(fd, &buff, 1) == 0)
		return 0;
	
	if(write(fd, r5, sizeof(r5)) == 0)
		return 0;
	
	buff = '\n';
	
	if(write(fd, &buff, 1) == 0)
		return 0;
	
	return 1;
	
}

int EraseLine(int fd, char repl)
{
    char buff;
    int ret;
    do
    {
        if((ret = read(fd, &buff, 1)) == -1)
        {
            if(ret == 0)
                return 1;
            else
                return -1;
        }
        
        lseek(fd, -1, SEEK_CUR);
        
        if(buff != ';' && buff != ',' && buff != '\n')
            buff = repl;
        
        if(write(fd, &buff, 1) == -1)
        {
            return -1;
        }
    }
    while(buff != '\n');
    
    return 1;
}

int PreviewLine(int fd)
{
    char buff;
    int ret;
    do
    {
        if((ret = lseek(fd, -2, SEEK_CUR)) < 1)
        {
            if(ret == 0)
                return 1;
            else
                return -1;
        }
        
        if(read(fd, &buff, 1) == -1)
        {
            return -1;
        }
    }
    while((buff != '\n'));
    
    return ret;
}


