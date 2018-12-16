.SILENT:

APPNAME=SNMP-Manager
COMPILATEUR=gcc
DEBUG=

Appli: $(APPNAME)

$(APPNAME):	SNMP-Manager.c
		$(COMPILATEUR) SNMP-Manager.c $(DEBUG) -o $(APPNAME)
		echo "Compile $(APPNAME)"


clobber:	clean
		rm -f ./$(APPNAME)
		echo Projet Nettoyé

clean:	
	rm -f ./*.o ./core
	echo Fichiers obj et core effacés
