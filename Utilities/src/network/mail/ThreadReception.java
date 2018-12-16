/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author jona1993
 */
public class ThreadReception extends Thread implements Runnable {
    private Account selectedAccount;
    private Session sess;
    private Store store;
    private File folder;
    private Properties props;
    private boolean Stop;
    private JTable table;
    private Message msgs[];
    
    public ThreadReception(Account compte, JTable t) {
        this.selectedAccount = compte;
        this.table = t;
        
        props = new Properties();
        
        props.put("mail." + selectedAccount.getProtocole() + ".host", selectedAccount.getMailserver());
        switch(selectedAccount.getProtocole())
        {
            case "imaps": props.put("mail." + selectedAccount.getProtocole() + ".port", "993");
                break;
            case "imap": props.put("mail." + selectedAccount.getProtocole() + ".port", "143");
                break;
            case "pop3" : props.put("mail." + selectedAccount.getProtocole() + ".port", "110");
                break;
            case "pop3s" : props.put("mail." + selectedAccount.getProtocole() + ".port", "995");
                break;
            
        }
        
        props.put("mail.store.protocol", selectedAccount.getProtocole());
        
        props.put("mail.smtp.host", selectedAccount.getSMTPserver());
        props.put("file.encoding", "utf-8");
        
        /////////////
        if(selectedAccount.isSSL())
        {
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.port", "465");
            props.put("mail.smtp.auth", "true");
            
            
            /*sess = Session.getDefaultInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(selectedAccount.getId(), selectedAccount.getPw());
                }
            });*/
            sess = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(selectedAccount.getId(), selectedAccount.getPw());
                }
            });
        }
        else
        {
            props.put("mail.smtp.port", "25");
            //sess = Session.getDefaultInstance(props, null);
            sess = Session.getInstance(props, null);
        }
        /////////////
        
        
        sess.setDebug(true);
        
        
    }
    
    
    
    @Override
    public void run() {
        boolean entree = true;
        Folder f = null;
        do
        {
            try
            {
                if(!entree)
                {
                    Thread.sleep(120000);
                }
                else
                {
                    entree = false;
                }
                
                synchronized(this)
                {
                    if(store != null)
                        if(store.isConnected())
                        {
                            if(f == null)
                                f.close(true);
                            
                            store.close();
                        }
                    
                    store = sess.getStore(selectedAccount.getProtocole());

                    store.connect(selectedAccount.getId(), selectedAccount.getPw());



                    System.out.println("Connected !");

                    f = store.getFolder("INBOX");

                    f.open(Folder.READ_WRITE); // Pour la suppression des messages

                    msgs = f.getMessages();
                    DefaultTableModel model = new DefaultTableModel();

                    model.addColumn("N°");
                    model.addColumn("Pièce Jointe");
                    model.addColumn("Subject");
                    model.addColumn("Correspondants");
                    model.addColumn("Date");
                    String row[];
                    Multipart mp;
                    BodyPart bp;
                    for(Message m: msgs) {
                        // En réalité si POP, on sérialise les messages (mais pas l'objet Message -> Pas sérializable)
                        row = new String[5];

                        row[0] = String.valueOf(m.getMessageNumber());

                        if(m.getContent() instanceof Multipart)
                        {
                            mp = (Multipart)m.getContent();
                            row[1] = String.valueOf(mp.getCount()-1);
                        }
                        else
                        {
                            row[1] = "0";
                        }


                        row[2] = m.getSubject();
                        Address[] adds = m.getFrom();
                        row[3] = "";
                        
                        if(adds != null)
                        {
                            for(Address a: adds)
                            {
                                    row[3] += a.toString() + ";";
                            }
                        }
                        

                        if(m.getSentDate() != null)
                            row[4] = m.getSentDate().toString();
                        else
                            row[4] = "NEANT";

                        model.addRow(row);
                    }
                    table.setModel(model);

                    
                }

                

            } catch (NoSuchProviderException ex) {
                Logger.getLogger(ThreadReception.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MessagingException ex) {
                Logger.getLogger(ThreadReception.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                System.err.println("Interrupted");
            } catch (IOException ex) {
                Logger.getLogger(ThreadReception.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        while(!Stop);
    }
    
    public Message[] getMessages(){
        return msgs;
    }
    
    public void Stop() throws MessagingException
    {
        Stop = true;
        
        synchronized(this)
        {
            if(store != null)
                if(store.isConnected())
                {
                    store.close();
                }
        }
    }
    
    public void CloseStore() throws MessagingException {
        synchronized(this)
        {
            
            if(store != null)
                if(store.isConnected())
                {
                    store.close();
                }
            
            
        }
    
    }

    public Session getSession() {
        return sess;
    }
    
    
}
