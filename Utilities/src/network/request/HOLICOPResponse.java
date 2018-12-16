/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network.request;

import java.io.Serializable;
import network.request.interfaces.Reponse;

/**
 *
 * @author jona1993
 */
public class HOLICOPResponse implements Reponse, Serializable {
    public final static int OK = 1;
    public final static int NOK = 2;
    public final static int ERROR = -1; 
    
    private int codeRetour;
    private String chargeUtile;
    private byte[] digest;
    private long time;
    private double alea;
    public String cmd;
    
    
    public HOLICOPResponse(int codeRetour, String chargeUtile, String cmd) {
        this.codeRetour = codeRetour;
        this.chargeUtile = chargeUtile;
        this.cmd = cmd;
    }

    public HOLICOPResponse(int codeRetour, String chargeUtile, byte[] digest, long time, double alea, String cmd) {
        this.codeRetour = codeRetour;
        this.chargeUtile = chargeUtile;
        this.digest = digest;
        this.time = time;
        this.alea = alea;
        this.cmd = cmd;
    }
    
    

    @Override
    public int getCode() {
        return codeRetour;
    }

    public String getChargeUtile() {
        return chargeUtile;
    }

    public void setChargeUtile(String chargeUtile) {
        this.chargeUtile = chargeUtile;
    }

    public byte[] getDigest() {
        return digest;
    }

    public long getTime() {
        return time;
    }

    public double getAlea() {
        return alea;
    }

    public void setDigest(byte[] digest) {
        this.digest = digest;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setAlea(double alea) {
        this.alea = alea;
    }
    
    
    
}
