/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database.facility;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
/**
 *
 * @author jona1993
 */

// ICI, la connexion sera créée
public class DBConnect {
    
    private Connection connection;
    private Statement statement;
    private PreparedStatement pstatement;
    public DBConnect(String arg, String id, String pw) throws SQLException, ClassNotFoundException {
        
        if(arg.contains("mysql"))
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        else if(arg.contains("oracle"))
        {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        }
        
        connection = DriverManager.getConnection(arg, id, pw);
        
        connection.setAutoCommit(false);
        
        statement = connection.createStatement();
    }
    
    @Deprecated
    public synchronized ResultSet Request(String arg) throws SQLException {
        return statement.executeQuery(arg);
    }
    
    public synchronized int UpdatePrepared(String arg, Hashtable<Integer, Object> hash) throws SQLException {
        int ret;
        
        pstatement = connection.prepareStatement(arg);
        
        for(int i = 0; i < hash.size(); i+=1){
            if(hash.get(i).toString().compareTo("NULL") == 0)
                pstatement.setNull(i+1, java.sql.Types.JAVA_OBJECT);
            else
            pstatement.setObject(i+1, hash.get(i)); // A condition que le type soit compatible, sinon Exception
        }
       
        ret = pstatement.executeUpdate();
        
        connection.commit();
        
        return ret;
    }
    
    public synchronized ResultSet SelectPrepared(String arg, Hashtable<Integer, Object> hash) throws SQLException {
        
        pstatement = connection.prepareStatement(arg);
        
        for(int i = 0; i < hash.size(); i+=1){
            if(hash.get(i).toString().compareTo("NULL") == 0)
                pstatement.setNull(i+1, java.sql.Types.JAVA_OBJECT);
            else
            pstatement.setObject(i+1, hash.get(i)); // A condition que le type soit compatible, sinon Exception
        }
      
        
        return pstatement.executeQuery();
    }
    
    public synchronized Connection getConnection(){
        return connection;
    }
    
    public synchronized void Close() throws SQLException {
        connection.close();
    }
}
