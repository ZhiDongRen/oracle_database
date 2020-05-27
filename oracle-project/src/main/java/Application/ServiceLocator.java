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
     * ���ڷǾ�̬��Ƶķ���λ�����캯��
     */

    /**
     * �����������ݿ�
     * @return
     * @throws SQLException ������ݿ�����ʧ��
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
     * ������¼����
     * @return
     */
    public static Authenticator getAuthenticator() {
        if (ServiceLocator.authenticator == null) {
            ServiceLocator.authenticator = new Authenticator();
        }
        return ServiceLocator.authenticator;
    }
}

