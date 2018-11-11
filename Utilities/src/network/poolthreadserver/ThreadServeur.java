/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network.poolthreadserver;

import java.io.FileInputStream;
import network.request.interfaces.Requete;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.request.interfaces.Reponse;

/**
 *
 * @author jona1993
 */
public class ThreadServeur extends Thread {
    private int port;
    private SourceTaches tasksexec;
    private ConsoleServeur app;
    private ServerSocket soc = null;
    private int maxClients;
    public static int nbClients = 0;
    
    public ThreadServeur(int p, SourceTaches st, ConsoleServeur cs){
        port = p;
        tasksexec = st;
        app = cs;
        
        Properties prop = new Properties();
        InputStream in = null;

        try
        {
            in = new FileInputStream("settings.properties");
            
            prop.load(in);
            
            maxClients = Integer.parseInt(prop.getProperty("MaxClientServer"));
            
        }
        catch(IOException e)
        {
            System.err.println(e.getMessage());
        }
        finally
        {
            if(in != null)
                try {
                    in.close();
            } catch (IOException ex) {
                    System.err.println(ex.getMessage());
            }
        }
    }
    
    public void run(){
        try
        {
            soc = new ServerSocket(port);
            
        }
        catch(IOException e)
        {
            System.err.println(e.getMessage());
        }
        
        for(int i = 0; i < maxClients; i += 1){
            ThreadClient ths = new ThreadClient(tasksexec, "Thread du pool n° " + String.valueOf(i));

            ths.start();
        }
        
        Socket socServ = null;
        
        while(!interrupted()){
            try
            {
                System.err.println("Serveur en Attente...");
                socServ = soc.accept();
                app.TraceEvents(socServ.getRemoteSocketAddress().toString() + "#accept#thread serveur");
                
            }
            catch(IOException e)
            {
                System.err.println("Err accept");
            }
            
            
            ObjectInputStream ois = null;
            ObjectOutputStream oos = null;
                
            if(nbClients < maxClients){
                
                Requete req = null;
                
                
                try
                {
                    
                    ois = new ObjectInputStream(socServ.getInputStream());
                    oos = new ObjectOutputStream(socServ.getOutputStream());
                    
                    req = (Requete)ois.readObject();
                    
                    req.setOIS(ois);
                    req.setOOS(oos);

                }
                catch(ClassNotFoundException | IOException e)
                {
                    System.err.println(e.getMessage());
                }
                
                nbClients += 1;

                Runnable work = req.createRunnable(socServ, app);
                
                if(work != null){
                    tasksexec.recordTask(work);

                }
            }
            else
            {
                Reponse rep;
                    
                rep = new Reponse() {
                    @Override
                    public int getCode() {
                        return -1;
                    }
                };

                try {
                    
                    oos = new ObjectOutputStream(socServ.getOutputStream());
                    oos.writeObject(rep);
                    oos.flush();
                            
                } catch (IOException ex) {
                    Logger.getLogger(ThreadServeur.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                app.TraceEvents("Serveur Surchargé#accept#thread serveur");
            }
            
        }
    }
   
}
