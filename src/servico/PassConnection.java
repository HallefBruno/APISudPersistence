
package servico;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import util.Excecao;

/**
 *
 * @author hallef.sud
 */
public class PassConnection extends BankOperation {
    
    public static Connection oConn = getConectionFactory();
    public static Statement oStm;
    public static ResultSet oSet;
    
    public static Connection getTransaction() {
        if(oConn!=null) return oConn;
        return null;
    }
    
    //Usar esse método para obter a conexão da classe de conexão de terceiros
    public static Connection getTheConnection(Connection oConn) {
        return PassConnection.oConn = oConn;
    }
    
    public static void closeConnection() {
        if (oConn != null) {
            try {
                oConn.close();
                oStm.close();
            }catch(SQLException e) {
                throw new Excecao(PassConnection.class, "closeConnection", e);
            }
        }
    }
}
