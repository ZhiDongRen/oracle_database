package Model;

import Application.ServiceLocator;
import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.pool.OracleDataSource;

/**
 *
 * @author Doria
 */
public class Reload {

    private static final String COMMANDS_DELIMITER = ";";
    private static final String COMMANDS_TRIGGER = "; /";

    /**
     *
     * @throws SQLException
     */
  

    private static void loadSqlScript(String filename) throws SQLException {
        Reload.loadSqlScript(Reload.COMMANDS_DELIMITER, filename);
    }

    private static void loadSqlScript(String delimiter, String filename) throws SQLException {
        String originalLine, path, modifiedString, pattern, instructions;
        StringBuilder sb = new StringBuilder();
        FileReader fr;
        try {
            path = new File("").getCanonicalPath();
            path = path.concat("/sql/" + filename);
            System.out.println(path);
            fr = new FileReader(new File(path));
        } catch (IOException ex) {
            Logger.getLogger(Reload.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        try (BufferedReader br = new BufferedReader(fr)) {
            while ((originalLine = br.readLine()) != null) {
                pattern = "(.*)(--.*)$";
                modifiedString = originalLine.replaceAll(pattern, "$1");
                if (!modifiedString.trim().equals("") || !modifiedString.trim().equals("/")) {
                    sb.append(modifiedString).append(" ");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Reload.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        modifiedString = sb.toString();
        String[] inst = modifiedString.split(delimiter);

        OracleDataSource ods = ServiceLocator.getConnection();
        try (Connection conn = ods.getConnection()) {

            for (String inst1 : inst) {
                instructions = inst1.trim();
                if (!instructions.equals("")) {
                    try (Statement stmt = conn.createStatement()) {
                        if (delimiter.equals(Reload.COMMANDS_TRIGGER)) {
                            instructions = instructions.concat(Reload.COMMANDS_DELIMITER);
                            stmt.executeUpdate(instructions);
                        } else {
                            stmt.executeQuery(instructions);
                        }
                    } catch (SQLException ex) {
                        System.out.println(">>" + instructions + "<<");
                        Logger.getLogger(Reload.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        }

    }
      /**
     *
     * @return
     */
    public static boolean isReloadRequired() {
        int number_of_table = 0;
        try {
            OracleDataSource ods = ServiceLocator.getConnection();

            try (Connection conn = ods.getConnection(); Statement stmt = conn.createStatement(); ResultSet rset = stmt.executeQuery(
                    "select count(*) as number_of_table from user_tables where table_name in ('ESTATE','ESTATEPIC','layout')")) {
                while (rset.next()) {
                    number_of_table = rset.getInt("number_of_table)");
                }
            }
        } catch (SQLException sqlEx) {
            return false;
        }
        return number_of_table != 3;
    }
    /**
     *
     * @return
     */
    public static boolean isConnectionValid() {
        try {
            OracleDataSource ods = ServiceLocator.getConnection();

            try (Connection conn = ods.getConnection(); Statement stmt = conn.createStatement(); ResultSet rset = stmt.executeQuery(
                    "select 1+2.0 as col1, 'foo' as col2 from dual")) {
                while (rset.next()) {
                }
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
            return false;
        }
        return true;
    }
}