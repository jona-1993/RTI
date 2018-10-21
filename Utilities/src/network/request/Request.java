/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network.request;
import database.facility.DBRequest;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.poolthreadserver.ConsoleServeur;
import network.request.interfaces.Requete;

/**
 *
 * @author jona1993
 */
public class Request implements Requete, Serializable {
    public final static int REQUEST_FUCAMP = 1;
    public final static int REQUEST_ROMP = 2;
    private int type;
    private String chargeUtile;
    public String cmd;
    private transient Socket soc;
    private transient ObjectInputStream ois = null;
    private transient ObjectOutputStream oos = null;
    private transient DBRequest connect = null;
    private transient Hashtable<String, String> hashdb;
    
    public Request(int t, String chu, String c, Socket s){
        type = t;
        setChargeUtile(chu);
        soc = s;
        cmd = c;
    }
    
    public void setChargeUtile(String chu){
        chargeUtile = chu;
    }
    
    public void AccessDB(String arg, String id, String pass, boolean enable) throws ClassNotFoundException, SQLException{

        if(enable == true)
            connect = new DBRequest(arg, id, pass);
        else
            if(connect != null)
                connect.Close();
    }
    
    @Override
    public Runnable createRunnable(final Socket s, final ConsoleServeur cs) {
        
            
            switch(type)
            {
                case REQUEST_FUCAMP:
                    return new Runnable() {
                        @Override
                        public void run() {
                            Activites(s, cs);
                        }
                    };
                case REQUEST_ROMP:
                    return new Runnable() {
                        @Override
                        public void run() {
                            Reservations(s, cs);
                        }
                    };
                
            }
            return null;
        
    }
    
    public void Activites(Socket s, ConsoleServeur cs){
        
        FUCAMPResponse rep;
        Hashtable<Integer, Object> hash;
        ResultSet result;
        
        Properties prop = new Properties();
        InputStream in = null;


        hashdb = new Hashtable<String, String>();

        try
        {
            in = new FileInputStream("settings.properties");

            prop.load(in);

            hashdb.put("MySQLEnabled", prop.getProperty("MySQLEnabled"));
            if(hashdb.get("MySQLEnabled").compareTo("True") == 0)
            {
                hashdb.put("IpMySQL", prop.getProperty("IpMySQL"));
                hashdb.put("PortMySQL", prop.getProperty("PortMySQL"));
                hashdb.put("LoginMySQL", prop.getProperty("LoginMySQL"));
                hashdb.put("PasswordMySQL", prop.getProperty("PasswordMySQL"));

                AccessDB("jdbc:mysql://" + hashdb.get("IpMySQL") + ":" + hashdb.get("PortMySQL") 
                        + "/BD_HOLIDAYS?serverTimezone=UTC", hashdb.get("LoginMySQL") , 
                        hashdb.get("PasswordMySQL"), true);
            }
            else
                AccessDB(null, null, null, false);

        }
        catch(IOException e)
        {
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            if(in != null)
                try {
                    in.close();
            } catch (IOException ex) {
                    System.err.println(ex.getMessage());
            }
        }
        try
        {
            do
            {

                if(cmd.compareTo("Login") == 0)
                {

                    hash = new Hashtable<Integer, Object>();
                    hash.put(0, chargeUtile.split("#")[0]); // ID
                    hash.put(1, chargeUtile.split("#")[1]); // PASSWORD
                    System.err.println("ID: " + hash.get(0) + "\nPW: " + hash.get(1));
                    try
                    {
                        result = connect.SelectTable("USERS", "count(*)", "login = ? and password = ?", hash);
                        result.next();
                        if(result.getInt(1) == 1)
                        {
                            cmd = "Authenticated";
                            rep = new FUCAMPResponse(FUCAMPResponse.OK, null, "Login");
                        }
                        else
                            rep = new FUCAMPResponse(FUCAMPResponse.NOK, null, "Login");
                    }
                    catch(SQLException e)
                    {
                        System.err.println(e.getSQLState());
                        rep = new FUCAMPResponse(FUCAMPResponse.ERROR, null, "Login");
                    }
                    oos.writeObject(rep);
                    oos.flush();
                }
                else
                {
                    SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
                    rep = (FUCAMPResponse)ois.readObject();

                    System.err.println("COMMANDE: " + rep.cmd);
                        // Authenticated
                        switch(rep.cmd)
                        {
                            case "GetActivitiesDay":
                                try
                                {
                                    hash = new Hashtable<Integer, Object>();
                                    hash.put(0, 1);
                                    if(rep.getChargeUtile().compareTo("+") == 0)
                                        result = connect.SelectTable("ACTIVITES", "type, duree, prixHTVA", "duree > ?", hash);
                                    else
                                        result = connect.SelectTable("ACTIVITES", "type,  duree, prixHTVA", "duree = ?", hash);

                                    String send = "";

                                    while(result.next())
                                    {
                                        send += result.getString(1) + "/ " + result.getInt(2) + " jour(s) [" + result.getInt(3) + " euros]#";
                                    }
                                    if(send.length() > 0)
                                    {
                                        send = send.substring(0, send.length()-1);
                                        rep = new FUCAMPResponse(FUCAMPResponse.OK, send, "GetActivitiesDay");
                                    }
                                    else
                                        rep = new FUCAMPResponse(FUCAMPResponse.NOK, null, "GetActivitiesDay");
                                }
                                catch (SQLException ex) {
                                    Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
                                    rep = new FUCAMPResponse(FUCAMPResponse.ERROR, ex.getSQLState(), "GetActivitiesDay");
                                }
                                break;
                            case "RESERVER":
                                
                                
                                try
                                {
                                    if(connect.ReserverActivite(rep.getChargeUtile().split("#")[0], rep.getChargeUtile().split("#")[1], f.parse(rep.getChargeUtile().split("#")[2]), f.parse(rep.getChargeUtile().split("#")[3])) == 1)
                                        rep = new FUCAMPResponse(FUCAMPResponse.OK, null, "RESERVER");
                                    else
                                        rep = new FUCAMPResponse(FUCAMPResponse.NOK, null, "RESERVER");
                                }
                                catch (SQLException e)
                                {
                                    rep = new FUCAMPResponse(FUCAMPResponse.ERROR, e.getSQLState(), "RESERVER");
                                }
                                catch(ParseException e)
                                {
                                     rep = new FUCAMPResponse(FUCAMPResponse.ERROR, e.getMessage(), "RESERVER");
                                }
                                
                                break;
                            case "GetReservationsActivites":
                                try
                                {
                                    hash = new Hashtable<Integer, Object>();
                                    hash.put(0, connect.getLoginIdentity(rep.getChargeUtile()));
                                    result = connect.SelectTable("RESERVATIONS", "*", "voyageurtitulaire = ? and voyageurtitulaire < 100", hash); // ID à < 100 = Activités
                                    
                                    String send = "";
                                    
                                    while(result.next())
                                    {
                                        System.err.println(result.getString(1));
                                        send += "ID: " + result.getString(1) + " IDVOY: " + result.getInt(2) + " RESERV: " + result.getInt(3) + " DATE DEB: " + f.format(result.getDate(4)) + " DATE FIN: " + f.format(result.getDate(5)) + " PRIX NET: " + result.getInt(6) + " PAYE: " + result.getBoolean(7) + "#";
                                    }
                                    if(send.length() > 0)
                                    {
                                        send = send.substring(0, send.length()-1);
                                        rep = new FUCAMPResponse(FUCAMPResponse.OK, send, "GetReservationsActivites");
                                    }
                                    else
                                        rep = new FUCAMPResponse(FUCAMPResponse.NOK, null, "GetReservationsActivites");
                                }
                                catch (SQLException ex) {
                                    Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
                                    rep = new FUCAMPResponse(FUCAMPResponse.ERROR, ex.getSQLState(), "GetReservationsActivites");
                                }
                                break;
                            case "CancelReservation":
                                try
                                {
                                    hash = new Hashtable<Integer, Object>();
                                    hash.put(0, rep.getChargeUtile().split("#")[1]);
                                    hash.put(1, connect.getLoginIdentity(rep.getChargeUtile().split("#")[0]));
                                    hash.put(2, false);
                                    
                                    if(connect.DropTable("RESERVATIONS", hash, "id = ? and voyageurtitulaire = ? and paye = ?") == 1) // Pourquoi voyageur titulaire? Pour éviter que n'importe qui delete la réservation des autres
                                        rep = new FUCAMPResponse(FUCAMPResponse.OK, null, "CancelReservation");
                                    else
                                        rep = new FUCAMPResponse(FUCAMPResponse.NOK, null, "CancelReservation");
                                }
                                catch (SQLException e)
                                {
                                    rep = new FUCAMPResponse(FUCAMPResponse.ERROR, e.getSQLState(), "CancelReservation");
                                }
                                
                                break;
                        }

                        oos.writeObject(rep);

                }
            }
            while(s.isConnected());
        }
        catch(IOException | ClassNotFoundException e)
        {
            cs.TraceEvents("Client Déconnecté#Activites#" + Thread.currentThread().getName());
        }
        
    }
    
    
    public void Reservations(Socket s, ConsoleServeur cs){
        
        ROMPResponse rep;
        Hashtable<Integer, Object> hash;
        ResultSet result;
        
        Properties prop = new Properties();
        InputStream in = null;


        hashdb = new Hashtable<String, String>();

        try
        {
            in = new FileInputStream("settings.properties");

            prop.load(in);

            hashdb.put("MySQLEnabled", prop.getProperty("MySQLEnabled"));
            if(hashdb.get("MySQLEnabled").compareTo("True") == 0)
            {
                hashdb.put("IpMySQL", prop.getProperty("IpMySQL"));
                hashdb.put("PortMySQL", prop.getProperty("PortMySQL"));
                hashdb.put("LoginMySQL", prop.getProperty("LoginMySQL"));
                hashdb.put("PasswordMySQL", prop.getProperty("PasswordMySQL"));

                AccessDB("jdbc:mysql://" + hashdb.get("IpMySQL") + ":" + hashdb.get("PortMySQL") 
                        + "/BD_HOLIDAYS?serverTimezone=UTC", hashdb.get("LoginMySQL") , 
                        hashdb.get("PasswordMySQL"), true);
            }
            else
                AccessDB(null, null, null, false);

        }
        catch(IOException e)
        {
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            if(in != null)
                try {
                    in.close();
            } catch (IOException ex) {
                    System.err.println(ex.getMessage());
            }
        }
        try
        {
            do
            {

                if(cmd.compareTo("Login") == 0)
                {

                    hash = new Hashtable<Integer, Object>();
                    hash.put(0, chargeUtile.split("#")[0]); // ID
                    hash.put(1, chargeUtile.split("#")[1]); // PASSWORD
                    System.err.println("ID: " + hash.get(0) + "\nPW: " + hash.get(1));
                    try
                    {
                        result = connect.SelectTable("USERS", "count(*)", "login = ? and password = ? and VoyageurTitulaire is NULL", hash); // VoyageurTitulaire NULL = Agence
                        result.next();
                        if(result.getInt(1) == 1)
                        {
                            cmd = "Authenticated";
                            rep = new ROMPResponse(ROMPResponse.OK, null, "Login");
                        }
                        else
                            rep = new ROMPResponse(ROMPResponse.NOK, null, "Login");
                    }
                    catch(SQLException e)
                    {
                        System.err.println(e.getSQLState());
                        rep = new ROMPResponse(ROMPResponse.ERROR, null, "Login");
                    }
                    oos.writeObject(rep);
                    oos.flush();
                }
                else
                {
                    rep = (ROMPResponse)ois.readObject();
                    System.err.println("COMMANDE: " + rep.cmd);
                    // Authenticated
                    switch(rep.cmd)
                    {
                        case "GetFreeRooms":
                                try
                                {
                                    result = connect.getFreeRoom(new Date());

                                    String send = "";

                                    while(result.next())
                                    {
                                        send += "n°" + (result.getInt(1) - 100) + "/ MAX: " + result.getInt(2) + " Pers. PrixHTVA [" + result.getInt(3) + " euros] Cat: " + result.getString(4) + " Type: " + result.getString(5) + "#";
                                    }
                                    if(send.length() > 0)
                                    {   
                                        send = send.substring(0, send.length()-1);
                                        rep = new ROMPResponse(ROMPResponse.OK, send, "GetFreeRooms");
                                    }
                                    else
                                        rep = new ROMPResponse(ROMPResponse.NOK, null, "GetFreeRooms");
                                }
                                catch (SQLException ex) {
                                    Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
                                    rep = new ROMPResponse(ROMPResponse.ERROR, ex.getSQLState(), "GetFreeRooms");
                                }
                                break;
                        case "BROOM":
                            try
                            {
                                System.err.println(rep.getChargeUtile());
                                SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
                                
                                if(connect.ReserverChambre(rep.getChargeUtile().split("#")[0], Integer.parseInt(rep.getChargeUtile().split("#")[1]) , f.parse(rep.getChargeUtile().split("#")[2]), f.parse(rep.getChargeUtile().split("#")[3])) > 0){
                                    rep = new ROMPResponse(ROMPResponse.OK, null, "BROOM");
                                }
                                else
                                    rep = new ROMPResponse(ROMPResponse.NOK, null, "BROOM");
                            }
                            catch(ParseException e)
                            {
                                System.err.println(e.getMessage());
                                rep = new ROMPResponse(ROMPResponse.ERROR, e.getMessage(), "GetFreeRooms");
                            }
                            catch(SQLException e)
                            {
                                Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, e);
                                rep = new ROMPResponse(ROMPResponse.ERROR, e.getSQLState(), "GetFreeRooms");
                            }
                                break;
                        case "LROOMS":
                            try
                            {
                                SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
                                result = connect.SelectTable("RESERVATIONS", "*", "id >= 100", new Hashtable<Integer, Object>());
                                String send = "";
                                    
                                    while(result.next())
                                    {
                                        System.err.println(result.getString(1));
                                        send += "ID: " + result.getString(1) + " IDVOY: " + result.getInt(2) + " RESERV: " + result.getInt(3) + " DATE DEB: " + f.format(result.getDate(4)) + " DATE FIN: " + f.format(result.getDate(5)) + " PRIX NET: " + result.getInt(6) + " PAYE: " + result.getBoolean(7) + "#";
                                    }
                                    if(send.length() > 0)
                                    {
                                        send = send.substring(0, send.length()-1);
                                        rep = new ROMPResponse(ROMPResponse.OK, send, "LROOMS");
                                    }
                                    else
                                        rep = new ROMPResponse(ROMPResponse.NOK, null, "LROOMS");
                            }
                            catch(SQLException e)
                            {
                                rep = new ROMPResponse(ROMPResponse.ERROR, e.getSQLState(), "LROOMS");
                            }
                            break;
                        case "CROOM":
                            try
                                {
                                    hash = new Hashtable<Integer, Object>();
                                    hash.put(0, rep.getChargeUtile().split("#")[1]);
                                    hash.put(1, connect.getLoginIdentity(rep.getChargeUtile().split("#")[0]));
                                    hash.put(2, false);
                                    
                                    if(connect.DropTable("RESERVATIONS", hash, "id = ? and voyageurtitulaire = ? and paye = ?") == 1) // Pourquoi voyageur titulaire? Pour éviter que n'importe qui delete la réservation des autres
                                        rep = new ROMPResponse(ROMPResponse.OK, null, "CROOM");
                                    else
                                        rep = new ROMPResponse(ROMPResponse.NOK, null, "CROOM");
                                }
                                catch (SQLException e)
                                {
                                    rep = new ROMPResponse(ROMPResponse.ERROR, e.getSQLState(), "CROOM");
                                }
                            break;
                            case "PROOM":
                                try
                                {
                                    hash = new Hashtable<Integer, Object>();
                                    hash.put(0, true);
                                    hash.put(1, connect.getLoginIdentity(rep.getChargeUtile().split("#")[0]));
                                    hash.put(2, rep.getChargeUtile().split("#")[1]);
                                    
                                    
                                    
                                    if(connect.UpdateTable("paye", "RESERVATIONS", hash, "voyageurtitulaire = ? and id like ?") > 0)
                                        rep = new ROMPResponse(ROMPResponse.OK, null, "PROOM");
                                    else
                                        rep = new ROMPResponse(ROMPResponse.NOK, null, "PROOM");
                                }
                                catch (SQLException e)
                                {
                                    rep = new ROMPResponse(ROMPResponse.ERROR, e.getSQLState(), "PROOM");
                                }
                            break;
                    }
                    
                    oos.writeObject(rep);
                    oos.flush();
                    
                }
            }
            while(s.isConnected());
        }
        catch(IOException | ClassNotFoundException e)
        {
            cs.TraceEvents("Client Déconnecté#Activites#" + Thread.currentThread().getName());
        }
    }
    
    
    public Socket getSocket(){
        return soc;
    }
    
    @Override
    public void setOIS(ObjectInputStream s){
        ois = s;
    }

    @Override
    public ObjectInputStream getOIS() {
        return ois;
    }
    
    @Override
    public void setOOS(ObjectOutputStream s){
        oos = s;
    }

    @Override
    public ObjectOutputStream getOOS() {
        return oos;
    }
    
    
}
