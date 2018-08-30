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
    
    //Retorna os dados da tabela estrangeira
    public String getNameTabelForeign(String tableName) {
        StringBuilder sql = new StringBuilder();//sql.append("SELECT ccu.table_name AS foreign_table_name,kcu.column_name AS column_name_fk, tc.table_schema ");
        sql.append("SELECT tc.table_schema, kcu.column_name AS column_name_fk, ccu.table_name AS foreign_table_name ");
        sql.append("FROM information_schema.table_constraints AS tc ");
        sql.append("JOIN information_schema.key_column_usage AS kcu ");
        sql.append("ON tc.constraint_name = kcu.constraint_name ");
        sql.append("AND tc.table_schema = kcu.table_schema ");
        sql.append("JOIN information_schema.constraint_column_usage AS ccu ");
        sql.append("ON ccu.constraint_name = tc.constraint_name ");
        sql.append("AND ccu.table_schema = tc.table_schema ");
        sql.append("WHERE constraint_type = ").append("'").append("FOREIGN KEY")
           .append("'").append("AND tc.table_name = ").append("'").append(tableName).append("'").append(";");
        //No executor de sql, eu passo as colunas de retorno
        return ExecutorSQL.executaSqlRetorna(sql.toString(), "table_schema,foreign_table_name,column_name_fk");//foreign_table_name,column_name_fk,table_schema
    }
    
    public static void main(String[] args) {
        System.out.println(new Postgres().getNameTabelForeign("telefone"));
    }
}


//Obtenho informacoes de uma tabela atraves de uma chave primaria DB -> PG
//SELECT
//    tc.table_schema, 
//    tc.constraint_name, 
//    tc.table_name, 
//    kcu.column_name, 
//    ccu.table_schema AS foreign_table_schema,
//    ccu.table_name AS foreign_table_name,
//    ccu.column_name AS foreign_column_name 
//FROM 
//    information_schema.table_constraints AS tc 
//    JOIN information_schema.key_column_usage AS kcu
//      ON tc.constraint_name = kcu.constraint_name
//      AND tc.table_schema = kcu.table_schema
//    JOIN information_schema.constraint_column_usage AS ccu
//      ON ccu.constraint_name = tc.constraint_name
//      AND ccu.table_schema = tc.table_schema
//WHERE constraint_type = 'FOREIGN KEY' AND tc.table_name='telefone';