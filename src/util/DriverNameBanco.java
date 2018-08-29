
package util;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import servico.PassConnection;

/**
 *
 * @author hallef.sud
 */
public final class DriverNameBanco {
    
    private DriverNameBanco() {}
    
    public static String getDriverName() {
        try {
            return PassConnection.getTransaction().getMetaData().getDriverName();
        } catch (SQLException ex) {
            Logger.getLogger(DriverNameBanco.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new Excecao(DriverNameBanco.class, "getDriverName", null);
    }
}
