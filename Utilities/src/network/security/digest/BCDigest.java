/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network.security.digest;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Arrays;
import java.util.Date;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 *
 * @author jona1993
 */
public class BCDigest {
    private static final String codeProvider = "BC";
    private MessageDigest md;
    private long temps;
    private double alea;
    private ByteArrayOutputStream baos;
    private DataOutputStream bdos;

    public BCDigest(String name) throws NoSuchAlgorithmException, NoSuchProviderException {
        Security.addProvider(new BouncyCastleProvider());
        baos = new ByteArrayOutputStream();
        bdos = new DataOutputStream((baos));
        md = MessageDigest.getInstance(name, codeProvider);
        temps = (new Date()).getTime();
        alea = Math.random();
    }
    
    public byte[] CreateSaledDigest(String user, String pw) throws IOException {
        md.update(user.getBytes());
        md.update(pw.getBytes());
        temps = (new Date()).getTime();
        alea = Math.random();
        bdos.writeLong(temps);
        bdos.writeDouble(alea);
        md.update(baos.toByteArray());
        
        return md.digest();
    }
    
    public byte[] ReceiveSaledDigest(String user, String dig, long time, double alea) throws IOException{
        System.err.println("à digérer: " + user + ";" + dig + ";" + time + ";" + alea);
        md.update(user.getBytes());
        md.update(dig.getBytes());
        bdos.writeLong(time);
        bdos.writeDouble(alea);
        md.update(baos.toByteArray());
        
        return md.digest();
    }
    
    public byte[] CreateDigest(String msg)
    {
        md.update(msg.getBytes());
        
        return md.digest();
    }
    
    public boolean Checksum(String msg, byte[] dig) {
        
        System.err.println("DIG:" + Arrays.toString(dig));
        System.err.println("NEW DIG: " + Arrays.toString(md.digest()));
        md.update(msg.trim().getBytes());
        
        
        return MessageDigest.isEqual(dig, md.digest());
    }

    public long getTemps() {
        return temps;
    }

    public double getAlea() {
        return alea;
    }
    
    
}
