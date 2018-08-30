
package servico;

import configuracoes.Pom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Excecao;

/**
 *
 * @author hallef.sud
 */
public class BankOperation {
    
    private static Connection oConn;

    public static Connection getConectionFactory() {
        
        try {
            //Print msg sql
            Repositorio.sqlMensagem = Boolean.valueOf(Pom.SQLMSG.getValue());
            Class.forName("org.postgresql.Driver");//org.mariadb.jdbc.Driver
            oConn = DriverManager.getConnection(Pom.URL.getValue(),Pom.USER.getValue(),Pom.PASSWORD.getValue());
            if (oConn != null) {
                return oConn;
            }
        } catch (SQLException ex) {
            throw new Excecao(BankOperation.class, "getConectionFactory", ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BankOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static void closeConnection() {
        if(oConn!=null) {
            try {
                oConn.close();
            } catch (SQLException ex) {
                throw new Excecao(BankOperation.class, "closeConnection", ex);
            }
        }
    }
}