#include<stdlib.h>
#ifndef LIBCSV_H

	int ReadByte(int, char*, char*);
	int WriteByte(int, char*, char*);
	
	int ReadByte5(int, char*, char*, char*, char*, char*);
	int WriteByte5(int, char*, char*, char*, char*, char*);

    int PreviewLine(int);
    int EraseLine(int, char);
#endif
