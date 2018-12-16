/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network.snmp;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

/**
 *
 * @author jona1993
 */
public class SNMP {
    private TransportMapping transport = null;
    private CommunityTarget target = null;
    private Address targetAddress = null;
    private PDU pdu = null;
    private Snmp snmp = null;
    public final static int GET = PDU.GET;
    public final static int GET_NEXT = PDU.GETNEXT;
    public final static int GET_BULK = PDU.GETBULK;
    
    public SNMP(int version, String community) throws IOException {
        
        transport = new DefaultUdpTransportMapping();
        
        transport.listen();
        
        target = new CommunityTarget();
        
        switch(version)
        {
            case 1: target.setVersion(SnmpConstants.version1);
                break;
            case 2: target.setVersion(SnmpConstants.version2c);
                break;
        }
        
        target.setCommunity(new OctetString(community));
        
        target.setRetries(2);
        target.setTimeout(1500);
        
        snmp = new Snmp(transport);
        
    }
    
    public ResponseEvent get(String oid, String addr, int action) throws IOException {
        
        targetAddress = new UdpAddress(addr + "/161");
        
        target.setAddress(targetAddress);
        
        
        pdu = new PDU();
        pdu.add(new VariableBinding(new OID(oid)));
        pdu.setType(action);
        
        
        if(action == GET)
            return snmp.get(pdu, target);
        else if(action == GET_NEXT)
            return snmp.getNext(pdu, target);
        else
            return snmp.getBulk(pdu, target);
    }
    
    public ResponseEvent set(String oid, String data, String addr) throws IOException {
        
        targetAddress = new UdpAddress(addr + "/161");
        
        target.setAddress(targetAddress);
        
        pdu = new PDU();
        pdu.add(new VariableBinding(new OID(oid), new OctetString(data)));
        pdu.setType(PDU.SET);
        
        return snmp.set(pdu, target);
    }
    
    public void getAsync(String oid, String addr, int action, SNMPListener listener) throws InterruptedException, IOException {
        
        targetAddress = new UdpAddress(addr + "/161");
        
        target.setAddress(targetAddress);
        
        listener.setSnmpManager(snmp);
        
        pdu = new PDU();
        pdu.add(new VariableBinding(new OID(oid)));
        pdu.setType(action);
        
        snmp.send(pdu, target, null, listener);
        
        synchronized(snmp)
        {
            snmp.wait();
        }

        
    }
    
    public void walk(String addr, JTable table) {
        
        targetAddress = new UdpAddress(addr + "/161");
        
        target.setAddress(targetAddress);
        
        TreeUtils tree = new TreeUtils(snmp, new DefaultPDUFactory());
        List<TreeEvent> events = tree.getSubtree(target, new OID("1"));
        
        DefaultTableModel model = new DefaultTableModel();
        Vector data;
        model.addColumn("OID");
        model.addColumn("Type");
        model.addColumn("Value");
        
        if(events == null || events.isEmpty()) {
            return;
        }
        
        for(TreeEvent e : events) {
            if(e == null)
                continue;
            
            if(e.isError())
                continue;
            
            VariableBinding[] varBindings = e.getVariableBindings();
            
            if(varBindings == null)
                continue;
            
            for(VariableBinding v : varBindings) {
                if(v == null)
                    continue;
                
                data = new Vector();
                data.add(v.getOid());
                data.add(v.getVariable().getSyntaxString());
                data.add(v.getVariable().toString());

                model.addRow(data);
                
            }
            table.setModel(model);
        }
    }
    
}
