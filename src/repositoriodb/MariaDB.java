package repositoriodb;

import util.Excecao;
import util.ExecutorSQL;

/**
 *
 * @author hallef.sud
 */
public class MariaDB {

    private static MariaDB instancia;
    private static Long valorSeq;

    protected MariaDB() {}

    public static MariaDB getInstancia() {
        if (instancia == null) {
            instancia = new MariaDB();
        }
        return instancia;
    }

    public Long getSequence(String tabela, String schema) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_NAME = '")
           .append(tabela).append("'").append(" AND ").append("TABLE_SCHEMA = '").append(schema).append("'");
        valorSeq = Long.valueOf(ExecutorSQL.executaSqlRetorna(sql.toString(), "AUTO_INCREMENT"));
        if(valorSeq!=null) {
            return (valorSeq-1);
        }
        throw new Excecao(MariaDB.class, "getSequence", null);
    }
    
    public String getColumnPrimeyKey(String tabela) {
        StringBuilder sql = new StringBuilder();
        String nomeColumn;
        sql.append("SELECT COLUMN_NAME ");
        sql.append("FROM information_schema.COLUMNS ");
        sql.append("WHERE TABLE_NAME = '").append(tabela).append("' ");
        sql.append("AND EXTRA = '").append("AUTO_INCREMENT").append("'");
        nomeColumn = ExecutorSQL.executaSqlRetorna(sql.toString(), "COLUMN_NAME");
        if(nomeColumn!=null) {
            return nomeColumn;
        }
        throw new Excecao(MariaDB.class, "getColumnPrimeyKey", null);
    }
    
    public String getNameTabelForeign(String tableName) {
        StringBuilder sql = new StringBuilder();
        String nomeColumn;
        sql.append("SELECT CONSTRAINT_SCHEMA,REFERENCED_TABLE_NAME,COLUMN_NAME ")
           .append("FROM information_schema.KEY_COLUMN_USAGE ")
           .append("WHERE TABLE_NAME = '").append(tableName).append("' ")
           .append("AND CONSTRAINT_NAME <> 'PRIMARY' ");
        nomeColumn = ExecutorSQL.executaSqlRetorna(sql.toString(), "CONSTRAINT_SCHEMA,REFERENCED_TABLE_NAME,COLUMN_NAME");
        if(nomeColumn!=null) {
            return nomeColumn;
        }
        throw new Excecao(MariaDB.class, "getNameTabelForeign", null);
    }
    
    public static Long getSequeValueMariaDB() {
    	return valorSeq;
    }
}
