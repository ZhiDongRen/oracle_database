
package Model;


import GUI.Icon;
import Application.ServiceLocator;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
 * 用于“"XRENZH00"."PRODUCT"  ”表的模型
 * @author Doria
 */
public class ImageModel extends Base {
     private static final String SQL_SELECT_DATA = "SELECT title FROM product WHERE code = ?";
    private static final String SQL_SELECT_IMAGE = "SELECT image FROM product WHERE code = ?";
    private static final String SQL_SELECT_IMAGE_FOR_UPDATE = "SELECT image FROM product WHERE code = ? FOR UPDATE";
    private static final String SQL_INSERT_NEW = "INSERT INTO product (code, title, image) VALUES (?, ?, ordsys.ordimage.init())";
    private static final String SQL_UPDATE_DATA = "UPDATE product SET title = ? WHERE code = ?";
    private static final String SQL_UPDATE_IMAGE = "UPDATE product SET image = ? WHERE code = ?";
    private static final String SQL_UPDATE_STILLIMAGE = "UPDATE product p SET p.image_si = SI_StillImage(p.image.getContent()) WHERE p.code = ?"; // an SQL method call needs to be on table.column, not just column
    private static final String SQL_UPDATE_STILLIMAGE_META = "UPDATE product SET image_ac = SI_AverageColor(image_si), image_ch = SI_ColorHistogram(image_si), image_pc = SI_PositionalColor(image_si), image_tx = SI_Texture(image_si) WHERE code = ?";
    private static final String SQL_SIMILAR_IMAGE = "SELECT dst.code, SI_ScoreByFtrList(new SI_FeatureList(src.image_ac,?,src.image_ch,?,src.image_pc,?,src.image_tx,?),dst.image_si) AS similarity FROM product src, product dst WHERE (src.code = ?) AND (src.code <> dst.code) ORDER BY similarity ASC";
    private int code;
    private String title;
    /**
     * 将具有指定路径“ path”的图像插入到“ title”
     * @param path
     * @param title
     * @return code 嵌入式图片。
     * @throws SQLException
     */
    public Integer insertImage(String path, int title) throws SQLException {
        
        Integer code;
        
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
                OracleResultSet rs = (OracleResultSet) stmt.executeQuery("image FROM product  ORDER BY code DESC FOR UPDATE");
                
                if (!rs.next()) {
                    return null;
                }
                
                OrdImage imgProxy = (OrdImage) rs.getORAData("image", OrdImage.getORADataFactory());
                code = rs.getInt("code");
                
                rs.close();
                
                try {
                    imgProxy.loadDataFromFile(path);
                    imgProxy.setProperties();
                }
                catch (IOException e) {
                    return null;
                }
                
                try (OraclePreparedStatement pstmt = (OraclePreparedStatement) conn.prepareStatement(SQL_UPDATE_DATA))
                {
                    pstmt.setORAData(1, imgProxy);
                    pstmt.setInt(2, code);
                    pstmt.executeUpdate();
                }
                
                try (OraclePreparedStatement pstmt = (OraclePreparedStatement) conn.prepareStatement("UPDATE product  o SET o.image_si = SI_StillImage(o.image.getContent()) WHERE code = ?"))
                {
                    pstmt.setInt(1, code);
                    pstmt.executeUpdate();
                }
                
                try (OraclePreparedStatement pstmt = (OraclePreparedStatement) conn.prepareStatement(SQL_UPDATE_STILLIMAGE))
                {
                    pstmt.setInt(1, code);
                    pstmt.executeUpdate();
                }
            }
            
            conn.commit();
        }
        
        return code;
    }
    
    /**
     * 搜索title图像。
     * @param title
     * @return 用户图像-键是数据库中的图像code，值是I类对象.
     * @throws SQLException
     */
    public Map<Integer, Icon> getImagesOftitle(int title) throws SQLException {
    
        Map<Integer, Icon> result = new HashMap<>();
        
        OracleDataSource ods = ServiceLocator.getConnection();
        try (Connection conn = ods.getConnection();
               OraclePreparedStatement pstmt = (OraclePreparedStatement)conn.prepareStatement("SELECT code, image FROM product  WHERE title = ?"))
        {
            pstmt.setInt(1, title);
            
            OracleResultSet rs = (OracleResultSet) pstmt.executeQuery();
            
            while (rs.next()) {
                OrdImage image = (OrdImage) rs.getORAData("image", OrdImage.getORADataFactory());
                byte[] tmp = image.getDataInByteArray();
                
                ImageIcon i = new ImageIcon(tmp);
                Icon tmpIcon = new Icon(i);
                result.put(rs.getInt("code"), tmpIcon);
            }
        } 
        catch (IOException e) {
            result = null;
        }
        
        return result;
    }
    
    /**
     * 返回具有指定code的图像。
     * @param code
     * @return 由字节字段表示的图像。
     * @throws SQLException
     */
    public byte[] getImage(Integer code) throws SQLException {
        

        byte[] result;
        OrdImage image;
        
        OracleDataSource ods = ServiceLocator.getConnection();
        try (Connection conn = ods.getConnection();
             OraclePreparedStatement pstmt = (OraclePreparedStatement)conn.prepareStatement(SQL_SELECT_IMAGE))
        {
            pstmt.setInt(1, code);
            
            OracleResultSet rs = (OracleResultSet) pstmt.executeQuery();
            
            if (!rs.next()) {
                result = null;
            }
            else {
                image = (OrdImage) rs.getORAData("image", OrdImage.getORADataFactory());

                result = image.getDataInByteArray();
            }
        } 
        catch (IOException e) {
            result = null;
        }
        
        return result;
    }
    
    /**
     * 删除具有指定code的图像。
     * @param code
     * @return 
     * @throws SQLException
     */
    public boolean delete(Integer code) throws SQLException {
        OracleDataSource ods = ServiceLocator.getConnection();
        try (Connection conn = ods.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM product  WHERE code = ?");
             )
        {
            stmt.setInt(1, code);
            return stmt.execute();
        }
    }
    
    /**
     * 通过调用旋转90度的过程来旋转具有指定code的图像
     * @param code
     * @throws SQLException
     */
    public void rotateImage(int code) throws SQLException {
        
        OracleDataSource ods = ServiceLocator.getConnection();
        try (Connection conn = ods.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement("CALL Rotate_image(?)");)
        {
            stmt.setInt(1,code);
            
            stmt.execute();
        }
    }
    
    /**
     * 根据code通过指定的图像查找最相似的图像。
     * @param code
     * @param weightAC
     * @param weightCH
     * @param weightPC
     * @param weightTX
     * @return 
     * @return相似图像-键是图像code，值是I型对象，图像按相似性排序。
     * @throws SQLException
     */
    public Map<Integer, Icon> getTheMostSimilar(Integer code, double weightAC, double weightCH, double weightPC, double weightTX) throws SQLException {
        Map<Integer, Icon> result = new LinkedHashMap<>();
        OracleDataSource ods = ServiceLocator.getConnection();
         try (Connection conn = ods.getConnection();
               OraclePreparedStatement pstmt = (OraclePreparedStatement)conn.prepareStatement(SQL_SIMILAR_IMAGE)
              )
        {
            //pstmt.setInt(1, title);
            pstmt.setDouble(1, weightAC);
            pstmt.setDouble(2, weightCH);
            pstmt.setDouble(3, weightPC);
            pstmt.setDouble(4, weightTX);
            pstmt.setInt(5, code);
            
            OracleResultSet rs = (OracleResultSet) pstmt.executeQuery();
            
            while (rs.next()) {
                OrdImage image = (OrdImage) rs.getORAData("image", OrdImage.getORADataFactory());
                byte[] tmp = image.getDataInByteArray();
                
                ImageIcon i = new ImageIcon(tmp);
                Icon tmpIcon = new Icon(i);
                tmpIcon.setScore(rs.getDouble("similarity"));
                result.put(rs.getInt("code"), tmpIcon);
            }
        } 
        catch (IOException e) {
            result = null;
        }
        
        return result;
    }


}