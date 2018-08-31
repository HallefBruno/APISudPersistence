
package util;

import servico.PassConnection;
import contrato.InfoBancoService;
import repositoriodb.MariaDB;
import repositoriodb.Postgres;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hallef.sud
 */
public class InfoBanco implements InfoBancoService {
    
    private String nameSchema;
    private String pk = "";
    private final String DRIVERNAME = DriverNameBanco.getDriverName();

    private static InfoBanco oInfoBanco;

    public static InfoBanco getInstancia() {
        if (oInfoBanco == null) {
            oInfoBanco = new InfoBanco();
        }
        return oInfoBanco;
    }
    
    /**
     *
     * @param schema
     * @param tabela
     * @return lista
     */
    @Override
    public List<String> getColumnsTabela(String schema, String tabela) {
        List<String> lista = new LinkedList<>();
        pk = "";
        if(tabela!=null && !tabela.isEmpty()) {
            Connection oConn = PassConnection.getTransaction();
            try {
                if(oConn!=null) {
                    DatabaseMetaData metadata = oConn.getMetaData();
                    ResultSet resultSet = metadata.getColumns(oConn.getCatalog(), schema, tabela, null);
                    pk = getPrimaryKey(schema, tabela);
                    while (resultSet.next()) {
                        if(!resultSet.getString("COLUMN_NAME").equals(pk)) {
                            lista.add(resultSet.getString("COLUMN_NAME"));
                        }
                    }
                    return lista;
                }
            }catch(SQLException e) {
                throw new Excecao(InfoBanco.class, "metodo getColumnsTabela ", e);
            }
        }
        throw new Excecao("Tabela inalida");
    }
    
    //Uso para comparar com os atributos anotados
    public List<String> getColumns(String schema, String tabela) {
        List<String> lista = new LinkedList<>();
        getPrimaryKey(schema, tabela);
        if(tabela!=null && !tabela.isEmpty()) {
            Connection oConn = PassConnection.getTransaction();
            try {
                if(oConn!=null) {
                    DatabaseMetaData metadata = oConn.getMetaData();
                    ResultSet resultSet = metadata.getColumns(oConn.getCatalog(), schema, tabela, null);
                    if(DRIVERNAME.contains("PostgreSQL") && Postgres.getInstancia().getIsAutoIncremento()) {
                        pk = getPrimaryKey(schema, tabela);
                    }
                    while (resultSet.next()) {
                        if(!resultSet.getString("COLUMN_NAME").equals(pk)) {
                            lista.add(resultSet.getString("COLUMN_NAME"));
                        }
                    }
                    return lista;
                }
            }catch(SQLException e) {
                throw new Excecao(InfoBanco.class, "metodo getColumns ", e);
            }
        }
        throw new Excecao("Tabela inalida");
    }
    
    public List<String> getColumnsAll(String schema, String tabela) {
        List<String> lista = new LinkedList<>();
        getPrimaryKey(schema, tabela);
        if(tabela!=null && !tabela.isEmpty()) {
            Connection oConn = PassConnection.getTransaction();
            try {
                if(oConn!=null) {
                    DatabaseMetaData metadata = oConn.getMetaData();
                    ResultSet resultSet = metadata.getColumns(oConn.getCatalog(), schema, tabela, null);
                    
                    while (resultSet.next()) {
                        lista.add(resultSet.getString("COLUMN_NAME"));
                    }
                    return lista;
                }
            }catch(SQLException e) {
                throw new Excecao(InfoBanco.class, "metodo getColumns ", e);
            }
        }
        throw new Excecao("Tabela inalida");
    }

    public boolean validaSchema(String schema,String tabela) {
        Connection oConn = PassConnection.getTransaction();
        if(oConn!=null) {
            if(schema.isEmpty()) {
                if(!getSchema(oConn, tabela).isEmpty()) {
                    return true;
                }
            } else {
                if(getSchema(oConn, tabela).equals(schema)) {
                    return true;
                }
            }
        } 
        return false;
    }
    
    //Obtem a chave primaria de uma tabela
    @Override
    public String getPrimaryKey(String schema, String tabela) {
        pk = "";
        if(tabela!=null && !tabela.isEmpty()) {
            Connection oConn = PassConnection.getTransaction();
            ResultSet oSet;
            try {
                if(oConn!=null) {
                    DatabaseMetaData metadata = oConn.getMetaData();
                    oSet = metadata.getPrimaryKeys(oConn.getCatalog(), schema, tabela);
                    if(DRIVERNAME.contains("MariaDB")) {
                        pk = MariaDB.getInstancia().getColumnPrimeyKey(tabela);
                    } else {
                        while(oSet.next()) {
                            pk = oSet.getString("COLUMN_NAME");// ou 4
                            if(pk!=null) {
                                return pk;
                            }
                        }
                    }
                }
            }catch(SQLException e) {
                throw new Excecao(InfoBanco.class, "metodo getPrimaryKey ", e);
            }
        }
        return pk;
    }
    
    @Override
    public String getForeignKey(String schema, String tabela)  {
        
        if(tabela!=null && !tabela.isEmpty()) {
            Connection oConn = PassConnection.getTransaction();
            ResultSet oSet;
            try {
                if(oConn!=null) {
                	
                    DatabaseMetaData oMetaData = oConn.getMetaData();
                    oSet = oMetaData.getImportedKeys(oConn.getCatalog(), schema, tabela);
                    while (oSet.next()) {
                        if(oSet.getString("FKCOLUMN_NAME")!=null) {
                            return oSet.getString("FKCOLUMN_NAME");
                        }
                    }
                }
            }catch(SQLException e) {
                throw new Excecao(InfoBanco.class, "metodo getForeignKey ", e);
            }
        }
    
        throw new Excecao("Tabela invalida ");
    }
    
    private String getSchema(Connection oConn, String tabela) {
    	
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM information_schema.TABLES where TABLE_NAME = ").append("'").append(tabela).append("'");
        Statement oStm;
        ResultSet oSet;
        if(oConn!=null) {
            try {
                oStm = oConn.createStatement();
                oSet = oStm.executeQuery(sql.toString());
                while(oSet.next()) {
                    setNameSchema(oSet.getString("TABLE_SCHEMA"));
                    return getNameSchema();
                }
            } catch (SQLException ex) {
                Logger.getLogger(InfoBanco.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        throw new Excecao(InfoBanco.class, "Essa tabela nao exite ou nao possui um schema "+tabela, null);
    }
    
    public Long getIdSequenceTable(String schema, String tabela) {
        if (!getPrimaryKey(schema, tabela).isEmpty()) {
            if (DRIVERNAME.contains("PostgreSQL")) {
                return Postgres.getInstancia().getSequence(schema, tabela, pk);
            } else if (DRIVERNAME.contains("MariaDB")) {
                return MariaDB.getInstancia().getSequence(tabela, schema);
            }
            
        } else if (pk.equals("")) {
            throw new Excecao(InfoBanco.class, "A tabela pai nao possui chave primaria ", null);
        }
        throw new Excecao(InfoBanco.class, "getIdSequenceTable", null);
    }

    public String getNameSchema() {
        return nameSchema;
    }

    public void setNameSchema(String nameSchema) {
        this.nameSchema = nameSchema;
    }
    
    public String getTableNameForeign(String tableName) {
        if(DRIVERNAME.contains("PostgreSQL")) {
            return Postgres.getInstancia().getNameTabelForeign(tableName);
        } else if (DRIVERNAME.contains("MariaDB")) {
            return MariaDB.getInstancia().getNameTabelForeign(tableName);
        }
        return null;
    }
}
