/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network.icmp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 *
 * @author jona1993
 */
public class Ping {
    private String address;
    private Process p = null;
    private String cmd;
    private BufferedReader br = null;

    public Ping(String address) {
        this.address = address;
        
        cmd = "ping -c 4 " + address;
    }
    
    public int isAlive() throws IOException {
        
        p = Runtime.getRuntime().exec(cmd);

        if(p == null)
        {
            return -1;
        }
        br = new BufferedReader(new InputStreamReader(p.getInputStream()));

        boolean notrep;
        String line;
        
        while((line = br.readLine()) != null) {
            
            if(Find100(line))
            {
                br.close();
                return 1;
            }
        }

        br.close();
        
        return 0;
    }
    
    boolean Find100(String s) {
        
        Scanner scan = new Scanner(s);
        
        while(scan.hasNext()) {
            if(scan.next().contains("100%")) {
                    return false;
            }
        }
        return true;
    }
    
    
}
