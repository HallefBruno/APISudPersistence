
package util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import servico.PassConnection;

/**
 *
 * @author hallef.sud
 */
public class ExecutorSQL {
    
    public static String executaSqlRetorna(String sql, String coluna) {
        Connection oConn = PassConnection.getTransaction();
        Statement oStm;
        ResultSet oSet;
        String col[] = null;
        List<String> colunas = new LinkedList<>();
        if(coluna!=null&&coluna.contains(",")) {
            col = coluna.split(",");
        }
        if (sql != null && !sql.isEmpty() && coluna != null && !coluna.isEmpty()) {
            if (oConn != null) {
                try {
                    oStm = oConn.createStatement();
                    oSet = oStm.executeQuery(sql);
                    if(col!=null&&col.length > 1) {
                        int cont = 0;
                        while (oSet.next()) {
                            while(col.length > cont) {
                                colunas.add(oSet.getString(col[cont]));
                                cont++;
                            }
                        }
                        return Arrays.asList(colunas).toString().replace("[[", "").replace("]]", "");
                    } else {
                        while (oSet.next()) {
                            return oSet.getString(coluna);
                        }
                    }
                } catch (SQLException ex) {
                    throw new Excecao(ExecutorSQL.class, "executaSqlRetorna", ex);
                }
            }
            //Caso nao seja necessario o retorno
        } else if (sql != null && !sql.isEmpty() && coluna != null && coluna.isEmpty()) {
            if (oConn != null) {
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
