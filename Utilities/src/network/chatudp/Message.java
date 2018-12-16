/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network.chatudp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import java.util.Scanner;
import network.security.digest.BCDigest;

/**
 *
 * @author jona1993
 */
public class Message {
    private String destination;
    private String sender;
    private String message;
    private static int sequenceNumber = 0;
    private String messageType;
    private SocketAddress address;
    private String content;
    private byte[] digest;
    private boolean valid = true;
    private BCDigest bcdigest;
    
    public Message(byte[] cont, SocketAddress socAdd) throws NoSuchAlgorithmException, NoSuchProviderException {
        address = socAdd;
        content = new String(cont, 0, cont.length);
        Scanner scan = new Scanner(content);
        scan.useDelimiter("#");
        
        
        messageType = scan.next();
        
        switch(messageType)
        {
            case "POST_QUESTION":
                int sizedig = Integer.parseInt(scan.next());
                scan.next();
                sequenceNumber += 1;
                destination = scan.next();
                sender = scan.next();
                String msgtodig = scan.next();
                message = "[?:" + sequenceNumber + "] " + msgtodig;
                
                digest = Arrays.copyOfRange(cont, ("POST_QUESTION#" + sizedig + "#").getBytes().length , ("POST_QUESTION#" + sizedig + "#").getBytes().length + sizedig);
                
                bcdigest = new BCDigest("MD5");
                
                message = "(" + destination + ")" + message;
                if(!(valid = bcdigest.Checksum(msgtodig, digest)))
                {
                    message = "(BROKEN) " + message;
                }
                
                break;
            case "ANSWER_QUESTION":
                sender = scan.next();
                sequenceNumber = Integer.parseInt(scan.next()); // On se met à jour par rapport aux autres 
                message = "(Reponse)[Q:" + sequenceNumber +  "] " + scan.next(); // [N°QUESTION] + Message
                break;
            case "POST_EVENT":
                sender = scan.next();
                message = scan.next();
                break;
        }
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public String getMessageType() {
        return messageType;
    }

    public SocketAddress getAddress() {
        return address;
    }

    public String getContent() {
        return content;
    }
    
    public boolean isValid(){
        return valid;
    }
    
}
