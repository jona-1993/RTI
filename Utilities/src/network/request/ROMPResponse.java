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
public class ROMPResponse implements Reponse, Serializable {
    public final static int OK = 1;
    public final static int NOK = 2;
    public final static int ERROR = -1; 
    
    private int codeRetour;
    private String chargeUtile;
    public String cmd;
    
    public ROMPResponse(int codeRetour, String chargeUtile, String cmd) {
        this.codeRetour = codeRetour;
        this.chargeUtile = chargeUtile;
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
    
    
    
}
