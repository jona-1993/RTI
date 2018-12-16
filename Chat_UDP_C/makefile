.SILENT:

APPNAME=chat_c
COMPILATEUR=gcc
DEBUG=

Appli: $(APPNAME)

$(APPNAME):	Chat.c
		$(COMPILATEUR) Chat.c -pthread $(DEBUG) -o $(APPNAME)
		echo "Compile $(APPNAME)"


clobber:	clean
		rm -f ./$(APPNAME)
		echo Projet Nettoyé

clean:	
	rm -f ./*.o ./core
	echo Fichiers obj et core effacés
