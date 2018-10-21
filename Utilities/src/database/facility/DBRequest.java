/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database.facility;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    
    
    
}
