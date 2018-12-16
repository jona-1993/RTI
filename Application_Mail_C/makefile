.SILENT:

APPNAME=mailclient
COMPILATEUR=gcc
DEBUG=
#-D REQUEST : Pour le mode REQUEST (Juste Login)

Appli: $(APPNAME)

$(APPNAME):	mailclient.c Network-Cli.o
		$(COMPILATEUR) mailclient.c Network-Cli.o $(DEBUG) -o $(APPNAME)
		echo "Compile $(APPNAME)"


Network-Cli.o:	Network-Cli.c Network-Cli.h
		$(COMPILATEUR) -c Network-Cli.c $(DEBUG)
		echo "Compile Network-Cli.o"

clobber:	clean
		rm -f ./$(APPNAME)
		echo Projet Nettoyé

clean:	
	rm -f ./*.o ./core
	echo Fichiers obj et core effacés
