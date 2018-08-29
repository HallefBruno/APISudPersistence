package repositoriodb;

import util.Excecao;
import util.ExecutorSQL;

/**
 *
 * @author hallef.sud
 */
public class Postgres {

    private static Postgres instancia;
    private static Long nextValeu = null;
    private boolean isAutoIncremento;
    private String strSeq = null;

    protected Postgres() {
    }

    public static Postgres getInstancia() {
        if (instancia == null) {
            instancia = new Postgres();
        }
        return instancia;
    }

    public Long getSequence(String schema, String tabela, String pk) {
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT PG_GET_SERIAL_SEQUENCE('").append(schema).append(".").append(tabela).append("'").append(",")
                .append("'").append(pk).append("'").append(") AS GETSEQ;");
        boolean executeProximoPasso;
        executeProximoPasso = isAutoIncrement(sql);

        sql = new StringBuilder();
        if (executeProximoPasso) {
            sql.append("SELECT NEXTVAL('").append(strSeq).append("') AS PROSEQ;");
            nextValeu = (Long.valueOf(ExecutorSQL.executaSqlRetorna(sql.toString(), "PROSEQ")) - 1);
            setValSeq();
            return nextValeu;
        }

        throw new Excecao(Postgres.class, "getSequence", null);
    }

    private boolean isAutoIncrement(StringBuilder sql) {
        strSeq = ExecutorSQL.executaSqlRetorna(sql.toString(), "GETSEQ");
        return strSeq.contains("seq");
    }
    
    //Setando seq para evitar desperdício de seq
    private void setValSeq() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT SETVAL('").append(strSeq).append("',").append(nextValeu).append(");");
        ExecutorSQL.executaSqlRetorna(sql.toString(),"");
    }

    public boolean getIsAutoIncremento() {
        return isAutoIncremento;
    }

    public static Long getSequeValuePostgres() {
        return nextValeu;
    }

}
