
package Model;

import GUI.myIcon;

import Application.ServiceLocator;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.jdbc.pool.OracleDataSource;
import oracle.ord.im.OrdImage;

/**

 */
public class ImageModel extends BaseModel {
    
     private static final String SQL_SELECT_DATA = "SELECT title FROM product WHERE id = ?";
    private static final String SQL_SELECT_IMAGE = "SELECT image FROM product WHERE id = ?";
    private static final String SQL_SELECT_IMAGE_FOR_UPDATE = "SELECT image FROM product WHERE id = ? FOR UPDATE";
    private static final String SQL_INSERT_NEW = "INSERT INTO product (id, title, image) VALUES (?, ?, ordsys.ordimage.init())";
    private static final String SQL_UPDATE_DATA = "UPDATE product SET title = ? WHERE id = ?";
    private static final String SQL_UPDATE_IMAGE = "UPDATE product SET image = ? WHERE id = ?";
    private static final String SQL_UPDATE_STILLIMAGE = "UPDATE product p SET p.image_si = SI_StillImage(p.image.getContent()) WHERE p.id = ?"; // an SQL method call needs to be on table.column, not just column
    private static final String SQL_UPDATE_STILLIMAGE_META = "UPDATE product SET image_ac = SI_AverageColor(image_si), image_ch = SI_ColorHistogram(image_si), image_pc = SI_PositionalColor(image_si), image_tx = SI_Texture(image_si) WHERE id = ?";
    private static final String SQL_SIMILAR_IMAGE = "SELECT dst.id, SI_ScoreByFtrList(new SI_FeatureList(src.image_ac,?,src.image_ch,?,src.image_pc,?,src.image_tx,?),dst.image_si) AS similarity FROM product src, product dst WHERE (src.id = ?) AND (src.id <> dst.id) ORDER BY similarity ASC";
    private int id;
    private String title;
    /**
     *  image
     * @param path
     * @param title
     * @return ID 
     * @throws SQLException
     */
    public Integer insertImage(String path, int title) throws SQLException {
        
        Integer id;
        
        OracleDataSource ods = ServiceLocator.getConnection();
        try (Connection conn = ods.getConnection();)
        {
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_NEW); )
            {
                stmt.setInt(1, title);
                
                stmt.executeUpdate();
            }
            
            try (Statement stmt = conn.createStatement(); )
            {
                OracleResultSet rs = (OracleResultSet) stmt.executeQuery("SELECT id, image FROM product ORDER BY id DESC FOR UPDATE");
                
                if (!rs.next()) {
                    return null;
                }
                
                OrdImage imageProxy = (OrdImage) rs.getORAData("image", OrdImage.getORADataFactory());
                id = rs.getInt("id");
                
                rs.close();
                
                try {
                    imageProxy.loadDataFromFile(path);
                    imageProxy.setProperties();
                }
                catch (IOException e) {
                    return null;
                }
                
                try (OraclePreparedStatement pstmt = (OraclePreparedStatement) conn.prepareStatement("UPDATE product SET image = ? WHERE id = ?"))
                {
                    pstmt.setORAData(1, imageProxy);
                    pstmt.setInt(2, id);
                    pstmt.executeUpdate();
                }
                
                try (OraclePreparedStatement pstmt = (OraclePreparedStatement) conn.prepareStatement("UPDATE product o SET o.image_si = SI_StillImage(o.image.getContent()) WHERE id = ?"))
                {
                    pstmt.setInt(1, id);
                    pstmt.executeUpdate();
                }
                
                try (OraclePreparedStatement pstmt = (OraclePreparedStatement) conn.prepareStatement("UPDATE product SET "
                        + "image_ac = SI_AverageColor(image_si), "
                        + "image_ch = SI_ColorHistogram(image_si), "
                        + "image_pc = SI_PositionalColor(image_si), "
                        + "image_tx = SI_Texture(image_si) "
                        + "WHERE id = ?"))
                {
                    pstmt.setInt(1, id);
                    pstmt.executeUpdate();
                }
            }
            
            conn.commit();
        }
        
        return id;
    }
    
    /**
     * 
     * @param title
     * @return 
     * @throws SQLException
     */
    public Map<Integer, myIcon> getImagesOftitle(int title) throws SQLException {
    
        Map<Integer, myIcon> result = new HashMap<>();
        
        OracleDataSource ods = ServiceLocator.getConnection();
        try (Connection conn = ods.getConnection();
               OraclePreparedStatement pstmt = (OraclePreparedStatement)conn.prepareStatement("SELECT id, image FROM product WHERE title = ?"))
        {
            pstmt.setInt(1, title);
            
            OracleResultSet rs = (OracleResultSet) pstmt.executeQuery();
            
            while (rs.next()) {
                OrdImage image = (OrdImage) rs.getORAData("image", OrdImage.getORADataFactory());
                byte[] tmp = image.getDataInByteArray();
              

                ImageIcon i = new ImageIcon(tmp);
                myIcon tmpIcon = new myIcon(i);
                result.put(rs.getInt("id"), tmpIcon);
                
            }
        } 
        catch (IOException e) {
            result = null;   
        }
        return result;
        
    }
    
    /**
     *
     * @param id
     * @return 
     * @throws SQLException
     */
    public byte[] getImage(Integer id) throws SQLException {
        

        byte[] result;
        OrdImage image;
        
        OracleDataSource ods = ServiceLocator.getConnection();
        try (Connection conn = ods.getConnection();
             OraclePreparedStatement pstmt = (OraclePreparedStatement)conn.prepareStatement("SELECT image FROM product WHERE id = ?"))
        {
            pstmt.setInt(1, id);
            
            OracleResultSet rs = (OracleResultSet) pstmt.executeQuery();
            
            if (!rs.next()) {
                result = null;
            }
            else {
                image = (OrdImage) rs.getORAData("image", OrdImage.getORADataFactory());
System.out.println(image);
                result = image.getDataInByteArray();
            }
        } 
        catch (IOException e) {
            result = null;
        }
        
        return result;
    }
    
    /**
     * 
     * @param id
     * @return 
     * @throws SQLException
     */
    public boolean delete(Integer id) throws SQLException {
        OracleDataSource ods = ServiceLocator.getConnection();
        try (Connection conn = ods.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM product WHERE id = ?");
             )
        {
            stmt.setInt(1, id);
            return stmt.execute();
        }
    }
    
    /**
     * 
     * @param id
     * @throws SQLException
     */
    public void rotateImage(int id) throws SQLException {
        
        OracleDataSource ods = ServiceLocator.getConnection();
        try (Connection conn = ods.getConnection(); 
            // PreparedStatement stmt = conn.prepareStatement("CALL Rotate_image(?)");)
                 PreparedStatement stmt = conn.prepareStatement("CALL Rotate_image(?)");)
        {
            stmt.setInt(1,id);
            
            stmt.execute();
        }
    }
    
    /**
     *
     * @param id
     * @param weightAC
     * @param weightCH
     * @param weightPC
     * @param weightTX
     * @return 
     * @throws SQLException
     */
    public Map<Integer, myIcon> getTheMostSimilar(Integer id, double weightAC, double weightCH, double weightPC, double weightTX) throws SQLException {
        Map<Integer, myIcon> result = new LinkedHashMap<>();
        OracleDataSource ods = ServiceLocator.getConnection();
         try (Connection conn = ods.getConnection();
               OraclePreparedStatement pstmt = (OraclePreparedStatement)conn.prepareStatement("SELECT dst.id, dst.image, SI_ScoreByFtrList("
                + "new SI_FeatureList(src.image_ac,?,src.image_ch,?,src.image_pc,?,src.image_tx,?),dst.image_si)"
                + " as similarity FROM product src, product dst "
                + "WHERE src.id = ? AND dst.id <> src.id ORDER BY similarity ASC")
              )
        {
            //pstmt.setInt(1, title);
            pstmt.setDouble(1, weightAC);
            pstmt.setDouble(2, weightCH);
            pstmt.setDouble(3, weightPC);
            pstmt.setDouble(4, weightTX);
            pstmt.setInt(5, id);
            
            OracleResultSet rs = (OracleResultSet) pstmt.executeQuery();
            
            while (rs.next()) {
                OrdImage image = (OrdImage) rs.getORAData("image", OrdImage.getORADataFactory());
                byte[] tmp = image.getDataInByteArray();
                
                ImageIcon i = new ImageIcon(tmp);
                myIcon tmpIcon = new myIcon(i);
                tmpIcon.setScore(rs.getDouble("similarity"));
                result.put(rs.getInt("id"), tmpIcon);
            }
        } 
        catch (IOException e) {
            result = null;
        }
        
        return result;
    }
public int getid() {
        return id;
    }
}