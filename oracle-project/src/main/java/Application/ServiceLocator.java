/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;
import Security.IIdentity;
import Security.Authenticator;
import java.sql.SQLException;
import oracle.jdbc.pool.OracleDataSource;


/**
 *
 * @author doria
 */
public class ServiceLocator {
 
    private static Authenticator authenticator = null;

    /**
     * 用于非静态设计的服务定位器构造函数
     */

    /**
     * 创建连接数据库
     * @return
     * @throws SQLException 如果数据库连接失败
     */
        public static OracleDataSource getConnection() throws SQLException {
        OracleDataSource ods = new OracleDataSource();
        IIdentity identity = ServiceLocator.getAuthenticator().getIdentity();
        ods.setURL("jdbc:oracle:thin:@//gort.fit.vutbr.cz:1521/orclpdb");
      //  System.out.println("User "+identity.getUsername());
        ods.setUser(identity.getUsername());
        ods.setPassword(identity.getPassword());
      //  System.out.println("Password "+identity.getPassword());
        return ods;
    }
   
 

    /**
     * 单个登录服务
     * @return
     */
    public static Authenticator getAuthenticator() {
        if (ServiceLocator.authenticator == null) {
            ServiceLocator.authenticator = new Authenticator();
        }
        return ServiceLocator.authenticator;
    }
}

