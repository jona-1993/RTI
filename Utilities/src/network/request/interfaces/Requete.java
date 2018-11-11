/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network.request.interfaces;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import network.poolthreadserver.ConsoleServeur;

/**
 *
 * @author jona1993
 */
public interface Requete {
    public Runnable createRunnable(Socket s, ConsoleServeur cs);
    
    public void setOIS(ObjectInputStream s);
    
    public void setOOS(ObjectOutputStream s);
    
    public ObjectInputStream getOIS();
    
    public ObjectOutputStream getOOS();
}
