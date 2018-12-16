/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network.mail;

import java.io.Serializable;

/**
 *
 * @author jona1993
 */
public class Account implements Serializable {
    private String libelle;
    private String address;
    private String protocole;
    private String id;
    private String pw;
    private String mailserver;
    private String SMTPserver;
    private boolean SSL;

    public Account() {
    }

    public Account(String libelle, String address, String protocole, String id, String pw, String mailserver, String SMTPserver, boolean SSL) {
        this.libelle = libelle;
        this.address = address;
        this.protocole = protocole;
        this.id = id;
        this.pw = pw;
        this.mailserver = mailserver;
        this.SMTPserver = SMTPserver;
        this.SSL = SSL;
    }

    
    
    public String getLibelle() {
        return libelle;
    }

    public String getAddress() {
        return address;
    }

    public String getProtocole() {
        return protocole;
    }

    public String getId() {
        return id;
    }

    public String getPw() {
        return pw;
    }

    public String getMailserver() {
        return mailserver;
    }

    public String getSMTPserver() {
        return SMTPserver;
    }

    public boolean isSSL() {
        return SSL;
    }
    

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setProtocole(String protocole) {
        this.protocole = protocole;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public void setMailserver(String mailserver) {
        this.mailserver = mailserver;
    }

    public void setSMTPserver(String SMTPserver) {
        this.SMTPserver = SMTPserver;
    }

    public void setSSL(boolean SSL) {
        this.SSL = SSL;
    }
    
    

    @Override
    public String toString() {
        return libelle;
    }
    
    
}
