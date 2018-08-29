
package util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import servico.PassConnection;

/**
 *
 * @author hallef.sud
 */
public class ExecutorSQL {
    
    public static String executaSqlRetorna(String sql,String coluna) {
        Connection oConn = PassConnection.getTransaction();
        Statement oStm = null;
        ResultSet oSet = null;
        
        if(sql!=null && !sql.isEmpty() && coluna!=null && !coluna.isEmpty()) {
            if(oConn!=null) {
                try {
                    oStm = oConn.createStatement();
                    oSet = oStm.executeQuery(sql);
                    while(oSet.next()) {
                        return oSet.getString(coluna);
                    }
                } catch (SQLException ex) {
                    throw new Excecao(ExecutorSQL.class, "executaSqlRetorna", ex);
                } 
            }
            
        } else if(sql!=null && !sql.isEmpty() && coluna!=null && coluna.isEmpty()) {
            if(oConn!=null) {
                try {
                    oStm = oConn.createStatement();
                    oStm.execute(sql);
                    
                } catch (SQLException ex) {
                    throw new Excecao(ExecutorSQL.class, "executaSqlRetorna", ex);
                } 
            }
        }
        return null;
    }
}
