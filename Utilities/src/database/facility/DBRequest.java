/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database.facility;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Hashtable;

/**
 *
 * @author jona1993
 */

// ICI, les requêtes seront écrites
public class DBRequest {
    private DBConnect connection;
    
    public static String COUNTVOYAGEURS = "Select * from VOYAGEURS";
    
    public DBRequest(String arg, String id, String pw) throws ClassNotFoundException, SQLException {
        connection = new DBConnect(arg, id, pw);
    }
    
    // /!\ SQLInjection ! /!\
    @Deprecated
    public synchronized ResultSet SendSimpleRequest(String arg) throws SQLException {
        return connection.Request(arg);
    }
    
    public synchronized int InsertTable(String columns, String table ,Hashtable<Integer, Object> hash) throws SQLException {
        String request = "insert into " + table + " (" + columns + ") values (";
        for(int i = 0; i < hash.size(); i+=1)
        {
            if(i != hash.size()-1)
                request += "?, ";
            else
                request += "?)";
        }
        return connection.UpdatePrepared(request, hash);
    }
    
    public synchronized int UpdateTable(String columns, String table ,Hashtable<Integer, Object> hash, String condition) throws SQLException {
        String request = "update " + table + " set ";
        String[] cols = columns.split(",");
        int i;
        
        for(i = 0; i < cols.length; i+=1)
        {
            if(i != cols.length-1)
                request += cols[i] + " = ? ,";
            else
                request += cols[i] + " = ?";
        }
        
        return connection.UpdatePrepared(request + " where " + condition, hash);
    }
    
    
    public synchronized int DropTable(String table ,Hashtable<Integer, Object> hash, String condition) throws SQLException {
        String request = "delete from " + table + " where " + condition;
        
        return connection.UpdatePrepared(request, hash);
    }
    
    //Ici, il s'agit d'une requête simple. Je devrai créer créer des Selects moins génériques si je veux faire des requêtes plus élaborées
    public synchronized ResultSet SelectTable(String tables, String select, String condition, Hashtable<Integer, Object> hash) throws SQLException{
        String request = "select " + select + " from " + tables;
        
        if(condition.length() > 0)
            request += " where " + condition;
        
        return connection.SelectPrepared(request, hash);
    }
    
    public synchronized DBConnect getConnection(){
        return connection;
    }
    
    public synchronized void Close() throws SQLException {
        connection.Close();
    }
    
    // No-Generic
    
    public synchronized int getLoginIdentity(String login) throws SQLException {
        Hashtable<Integer, Object> hash = new Hashtable<>();
        hash.put(0, login);
        String request = "select VoyageurTitulaire from USERS where login = ?";
        ResultSet result;
        result = connection.SelectPrepared(request, hash);
        
        result.next();
        
        if(result.getObject(1) != null)
        {
            return result.getInt(1);
        }
        else
            return 0;
    }
    
    public synchronized int getActiviteIdentity(String activite) throws SQLException {
        Hashtable<Integer, Object> hash = new Hashtable<>();
        hash.put(0, activite);
        String request = "select id from ACTIVITES where type = ?";
        ResultSet result;
        result = connection.SelectPrepared(request, hash);
        
        result.next();
        
        if(result.getObject(1) != null)
        {
            return result.getInt(1);
        }
        else
            return 0;
    }
    
    public synchronized int getFreeRoom(int numchambre, Date date) throws SQLException{ // Chambre libre ou pas..
        ResultSet result;
        Hashtable<Integer, Object> hash = new Hashtable<>();
        hash.put(0, numchambre +100);
        hash.put(1, date);
        hash.put(2, numchambre +100);
        
        String request = "select CHAMBRES.numero from CHAMBRES, RESERVATIONS where CHAMBRES.numero = ? "
                + "and CHAMBRES.numero = RESERVATIONS.reservation "
                + "and DATEDIFF(RESERVATIONS.datefin, ?) > 0 "
                + "union "
                + "select CHAMBRES.numero from CHAMBRES where CHAMBRES.numero = ? "
                + "and CHAMBRES.numero not in ("
                + "select reservation from RESERVATIONS)";
        
        
        result = connection.SelectPrepared(request, hash);
        
        
        if(result.next())
            return result.getInt(1);
        else
            return 0;
    }
    
    public synchronized ResultSet getFreeRoom(java.util.Date date) throws SQLException{ // Chambres libres
        ResultSet result;
        Hashtable<Integer, Object> hash = new Hashtable<>();
        hash.put(0, new Date(date.getTime()));
        
        String request ="select a.numero, a.nboccupants, a.prixHTVA, a.categorie, a.type "
                + "from CHAMBRES a "
                + "left join RESERVATIONS b " // Pas de Minus en MySQL? Laisse moi rire..
                + "on a.numero = b.reservation "
                + "where DATEDIFF(b.datefin, ?) >= 0 "
                + "or b.reservation is NULL";
        
        
        result = connection.SelectPrepared(request, hash);
        
        return result;
    }
    
    public synchronized int ReserverActivite(String login, String activite, java.util.Date datedebut, java.util.Date datefin) throws SQLException {
        int idVoy, idActi;
        String idRes;
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
        java.util.Date d = new java.util.Date();
        Hashtable<Integer, Object> hash;
        ResultSet result;
        
        idRes = f.format(d) + "-RES";
        
        result = SelectTable("RESERVATIONS", "count(*)", "", new Hashtable<Integer, Object>());
        result.next();
        idRes += (result.getInt(1) + 1);
        
        if((idVoy = getLoginIdentity(login)) != 0) {
            if((idActi = getActiviteIdentity(activite)) != 0)
            {
                hash = new Hashtable<>();
                hash.put(0, idRes);
                hash.put(1, idVoy);
                hash.put(2, idActi);
                hash.put(3, new Date(datedebut.getTime()));
                hash.put(4, new Date(datefin.getTime()));
                hash.put(5, "NULL");
                hash.put(6, false);
                
                
                String request = "insert into RESERVATIONS values (?, ?, ?, ?, ?, ?, ?)";
                
                connection.UpdatePrepared(request, hash);
                
                return 1;
            }
        }
        return 0;
    }
    
    
    public synchronized int ReserverChambre(String login, Integer numChambre, java.util.Date datedebut, java.util.Date datefin) throws SQLException {
        int idVoy;
        String idRes;
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
        java.util.Date d = new java.util.Date();
        Hashtable<Integer, Object> hash;
        ResultSet result;
        
        if((idVoy = getLoginIdentity(login)) > 0){
            
            if(getFreeRoom(numChambre, new Date(datefin.getTime())) > 0)
            {
                
                idRes = f.format(d) + "-RES";
                
                result = SelectTable("RESERVATIONS", "count(*)", "", new Hashtable<Integer, Object>());
                result.next();
                idRes += (result.getInt(1) + 1);


                hash = new Hashtable<>();
                hash.put(0, idRes);
                hash.put(1, idVoy);
                hash.put(2, numChambre + 100);
                hash.put(3, new Date(datedebut.getTime()));
                hash.put(4, new Date(datefin.getTime()));
                hash.put(5, "NULL");
                hash.put(6, false);


                String request = "insert into RESERVATIONS values (?, ?, ?, ?, ?, ?, ?)";

                connection.UpdatePrepared(request, hash);

                return 1;
            }
            else
                return 0;
            
        }
        else
            return -1;
    }
    
}
