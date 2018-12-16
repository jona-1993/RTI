/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network.chatudp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.poolthreadserver.ConsoleServeur;

/**
 *
 * @author jona1993
 */
public class Receiver extends Thread {
    
    private MulticastSocket soc;
    private boolean convers;
    private ConsoleServeur console;
    private DatagramPacket dp;
    private byte[] buffer;
    
    public Receiver(MulticastSocket s, ConsoleServeur cons){
        soc = s;
        convers = true;
        console = cons;
    }

    public void run() {
        while(convers)
        {
            
            buffer = new byte[65535];
            
            dp = new DatagramPacket(buffer, buffer.length);
            
            try
            {
                soc.receive(dp);
            } catch (IOException ex) {
                Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            try 
            {
                //String d = new String(dp.getData(), 0, dp.getLength());

                Message msg = new Message(dp.getData(), dp.getSocketAddress());

                System.out.println("TRACER = " + msg.getMessage());

                console.TraceEvents(msg.getSender() + " > " + msg.getMessage());
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchProviderException ex) {
                Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void Stop(){
        convers = false;
    }
    
    
}
