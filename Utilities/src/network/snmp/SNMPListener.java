
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package network.snmp;

import java.util.Vector;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

/**
 *
 * @author jona1993
 */
public class SNMPListener  implements ResponseListener {
    
    private Snmp snmpManager;
    private JTable table;
    
    public SNMPListener(Snmp snmpManager, JTable table) {
        this.snmpManager = snmpManager;
        this.table = table;
    }
    
    public SNMPListener(JTable table) {
        this.table = table;
    }

    @Override
    public void onResponse(ResponseEvent event) {
        
        ((Snmp)event.getSource()).cancel(event.getRequest(), this);
        System.out.println("Réponse reçue (PDU): " + event.getResponse());
        PDU rep = event.getResponse();
       
        DefaultTableModel model = new DefaultTableModel();
        Vector data;
        model.addColumn("OID");
        model.addColumn("Type");
        model.addColumn("Value");
        
        VariableBinding vb;
        Variable value;
        for (int i=0; i < rep.size(); i++)
        {
            vb = rep.get(i);
            value = vb.getVariable();
            
            data = new Vector();
            data.add(vb.getOid());
            data.add(value.getSyntaxString());
            data.add(value.toString());
            
            model.addRow(data);
            
        }
        table.setModel(model);
        
        synchronized(snmpManager)
        {
            snmpManager.notify();
        }
    }

    public void setSnmpManager(Snmp snmpManager) {
        this.snmpManager = snmpManager;
    }
    
    
    
    
}
