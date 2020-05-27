
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
 * ���ڡ�"XRENZH00"."PRODUCT"  �����ģ��
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
     * ������ָ��·���� path����ͼ����뵽�� title��
     * @param path
     * @param title
     * @return code Ƕ��ʽͼƬ��
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
     * ����titleͼ��
     * @param title
     * @return �û�ͼ��-�������ݿ��е�ͼ��code��ֵ��I�����.
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
     * ���ؾ���ָ��code��ͼ��
     * @param code
     * @return ���ֽ��ֶα�ʾ��ͼ��
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
     * ɾ������ָ��code��ͼ��
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
     * ͨ��������ת90�ȵĹ�������ת����ָ��code��ͼ��
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
     * ����codeͨ��ָ����ͼ����������Ƶ�ͼ��
     * @param code
     * @param weightAC
     * @param weightCH
     * @param weightPC
     * @param weightTX
     * @return 
     * @return����ͼ��-����ͼ��code��ֵ��I�Ͷ���ͼ������������
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