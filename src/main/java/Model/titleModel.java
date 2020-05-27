/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 *
 * @author doria
 */


import Application.ServiceLocator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;

import java.util.Map;        
import oracle.jdbc.pool.OracleDataSource;
        
        
public class titleModel extends BaseModel {
    
    /**
     *
     * @param id
     * @return get int t
     * @throws SQLException
     * @throws Exception
     */
    public Map<String,Object> get(int id) throws SQLException, Exception {
        
        Map<String,Object> row = new HashMap<>();
        
        OracleDataSource ods = ServiceLocator.getConnection();
        try (Connection conn = ods.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM product WHERE id = ?");
             )
        {
            stmt.setInt(1,id);

            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next()) {
                    row.put("id",id);
                    row.put("title",rs.getString("title"));
                }
                else {
                    return null;
                }
            }
        }
        
        return row; 
    
    }
    
    /**
     * getList
     * @return 
     * @throws SQLException
     */
    public Map<Integer, String> getList() throws SQLException {
        
        Map<Integer,String> listOfCustomers = new LinkedHashMap<>();
        
        OracleDataSource ods = ServiceLocator.getConnection();
        try (Connection conn = ods.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM product ORDER BY id");
             )
        {

            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next()) {
                    listOfCustomers.put(rs.getInt("id"), rs.getString("id") +" "+rs.getString("title"));
                }
            }
        }
        
        return listOfCustomers;
    }
    
    /**
     * insert into title
     * @param id
     * @param title
     * @return
     * @throws SQLException
     */
    public int insert(String id,String title ) throws SQLException {
    
        OracleDataSource ods = ServiceLocator.getConnection();
        try (Connection conn = ods.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO product (id,title) VALUES(?, ?)");
             )
        {
            stmt.setString(1,id);
             stmt.setString(2,title);
            stmt.execute();
            
            try (Statement stmt2 = conn.createStatement();
                 ResultSet rs = stmt2.executeQuery("SELECT id FROM product ORDER BY id DESC"))
            {
                rs.next();
                
                return rs.getInt("id");
            }
        }
    }
}
