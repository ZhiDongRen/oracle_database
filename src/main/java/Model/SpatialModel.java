package Model;


import Model.BaseModel;
import Application.ServiceLocator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import oracle.jdbc.pool.OracleDataSource;

/**
 *Doria
 */
public class SpatialModel extends BaseModel {
    
   

    
    /**
     * 
     * @param x
     * @param y
     * @return N¨¢zev objektu/budovy.
     * @throws SQLException
     * @throws Exception
     */
    public String getBuildingAtPoint(int x, int y) throws SQLException, Exception {
    
        int x1,x2,x3,y1,y2,y3;
        int r = 4;
        
        x1 = x3 = x;
        y1 = y - r;
        y3 = y + r;
        x2 = x + r;
        y2 = y;
        
        OracleDataSource ods = ServiceLocator.getConnection();
        try (Connection conn = ods.getConnection(); Statement stmt = conn.createStatement(); 
             ResultSet resultSet = stmt.executeQuery("select type from ESTATE2 where SDO_RELATE(shape, SDO_GEOMETRY(2003, NULL, NULL, SDO_ELEM_INFO_ARRAY(1,1003,4), SDO_ORDINATE_ARRAY("+x1+","+y1+", "+x2+","+y2+", "+x3+","+y3+")), 'mask=ANYINTERACT') = 'TRUE'"))
        {
            while (resultSet.next()) {
                return resultSet.getString("type");
            }
        }
        
        return null;
    }
    
    /**
     * .
     * @param name
     * @throws SQLException
     */
    public void deleteBuildingWithName(String name) throws SQLException {
        
        OracleDataSource ods = ServiceLocator.getConnection();
        try (Connection conn = ods.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM ESTATE2 WHERE type = ?");
             )
        {
            stmt.setString(1, name);
            stmt.execute();
        }
    
    }
    
    
    /**
     * 
     * @param name
     * @return
     * @throws SQLException
     */
    public double getAreaOfBuilding(String name) throws SQLException {
        OracleDataSource ods = ServiceLocator.getConnection();
        try (Connection conn = ods.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement("select SDO_GEOM.SDO_AREA(shape, 0.005) as area from ESTATE2 where type = ?")) 
        {
            stmt.setString(1, name);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getFloat("area");
                }
                else 
                    return 0;
            }
        }
    }
    
    /**
     *
     * @param name
     * @return
     * @throws SQLException
     */
    public double getLengthOfBuilding(String name) throws SQLException {
        OracleDataSource ods = ServiceLocator.getConnection();
        try (Connection conn = ods.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement("select SDO_GEOM.SDO_LENGTH(shape, 0.005) as length from ESTATE2 where type = ?")) 
        {
            stmt.setString(1, name);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getFloat("length");
                }
                else 
                    return 0;
            }
        }
    }
    
    /**
     *
     * @param name
     * @return
     * @throws SQLException
     */
    public Map<String, Float> getDistancesFromBuilding(String name) throws SQLException {
        
        Map<String, Float> result = new LinkedHashMap<>();
        
        OracleDataSource ods = ServiceLocator.getConnection();
        try (Connection conn = ods.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement("select a1.type as n1, a2.type as n2, SDO_GEOM.SDO_DISTANCE(a1.shape, a2.shape, 0.005) as distance from ESTATE2 a1, ESTATE2 a2 where a1.type = ? AND a1.type <> a2.type ORDER BY distance")) 
        {
            stmt.setString(1, name);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString("n2"), rs.getFloat("distance"));
                }
            }
        }
        
        return result;
    }
    
    /**
     * @param name
     * @param n 
     * @return 
     * @throws SQLException
     */
    public Map<String, Float> getNNearestNeighboursFromBuilding(String name, int n) throws SQLException {
        
        Map<String, Float> result = new LinkedHashMap<>();
        
        OracleDataSource ods = ServiceLocator.getConnection();
        try (Connection conn = ods.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement("select type, SDO_NN_DISTANCE(1) as distance from ESTATE2 where SDO_NN(shape, (SELECT x.shape FROM ESTATE2 x WHERE x.type='"+name+"'), 'sdo_batch_size=1', 1) = 'TRUE' AND ROWNUM <= ? AND type <> ? ORDER BY distance")) 
        {
            stmt.setString(2, name);
            stmt.setInt(1, n);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString("type"), (float)rs.getInt("distance"));
                }
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @throws SQLException
     */
    public void doUnionOperation() throws SQLException {
    
        OracleDataSource ods = ServiceLocator.getConnection();
        try (Connection conn = ods.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement("select distinct a1.type n1, a2.type n2 from ESTATE2 a1, ESTATE2 a2 WHERE a1.type<>a2.type AND SDO_RELATE(a1.shape, a2.shape,'MASK=OVERLAPBDYINTERSECT+CONTAINS+INSIDE') = 'TRUE' ORDER BY a1.type")) 
        {
            //stmt.setString(1, name);
            //stmt.setInt(2, n);
            
            boolean shouldEnd = false;
            
            while (!shouldEnd) {
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String n1 = rs.getString("n1");
                        String n2 = rs.getString("n2");

                        String newName = n1+"+"+n2;

                        PreparedStatement insertStmt = conn.prepareStatement("insert into ESTATE2 (type, shape) VALUES (?, SDO_GEOM.SDO_UNION((SELECT shape FROM ESTATE2 WHERE type=?),(SELECT shape FROM ESTATE2 WHERE type=?),0.005))");
                        PreparedStatement deleteStmt = conn.prepareStatement("delete from ESTATE2 where type IN (?,?)");
                        try {
                            insertStmt.setString(1, newName);
                            insertStmt.setString(2, n1);
                            insertStmt.setString(3, n2);

                            deleteStmt.setString(1, n1);
                            deleteStmt.setString(2, n2);

                            insertStmt.execute();
                            deleteStmt.execute();
                        }
                        finally {
                            insertStmt.close();
                            deleteStmt.close();
                        }
                    }
                    else {
                        shouldEnd = true;
                    }
                }
            }
        }
    }
}
