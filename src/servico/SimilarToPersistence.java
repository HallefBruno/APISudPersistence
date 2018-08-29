
package servico;

import anotacoes.Coluna;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import util.InfoBanco;


/**
 *
 * @author hallef.sud
 */
public class SimilarToPersistence extends PassConnection {
 
    public void salvar(String sql) {
        try {
            if (oConn != null) {
                oStm = null;
                try {
                    oConn.setAutoCommit(false);
                    oStm = oConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    oStm.executeUpdate(sql);
                    oConn.commit();
                } catch (SQLException ex) {
                    try {
                        oConn.rollback();
                    } catch (SQLException e) {
                        Logger.getLogger(InfoBanco.class.getName()).log(Level.SEVERE, "salvar", e);
                    }
                    Logger.getLogger(InfoBanco.class.getName()).log(Level.SEVERE, "salvar", ex);
                }
            }
            oConn.setAutoCommit(true);
        }catch(SQLException ex) {
            Logger.getLogger(InfoBanco.class.getName()).log(Level.SEVERE, "salvar", ex);
        }
    }
    
    public void alterar(String sql) {
        try {
            if(oConn!=null) {
                oStm = null;
                try {
                    oConn.setAutoCommit(false);
                    oStm = oConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    oStm.executeUpdate(sql);
                    oConn.commit();
                }catch(SQLException e) {
                    try {
                        oConn.rollback();
                    } catch (SQLException ex) {
                        Logger.getLogger(InfoBanco.class.getName()).log(Level.SEVERE, "alterar", ex);
                    }
                    Logger.getLogger(InfoBanco.class.getName()).log(Level.SEVERE, "alterar", e);
                }
            }
            oConn.setAutoCommit(true);
        }catch(SQLException ex) {
            Logger.getLogger(InfoBanco.class.getName()).log(Level.SEVERE, "alterar", ex);
        }
    }
    
    public void delete(String sql) {
        try {
            if(oConn!=null) {
                oStm = null;
                try {
                    oConn.setAutoCommit(false);
                    oStm = oConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    oStm.execute(sql);
                    oConn.commit();
                }catch(SQLException e) {
                    try {
                        oConn.rollback();
                    } catch (SQLException ex) {
                        Logger.getLogger(InfoBanco.class.getName()).log(Level.SEVERE, "alterar", ex);
                    }
                    if(!e.getSQLState().isEmpty()) {//.equals("23503") PG
                        JOptionPane.showMessageDialog(null, "Este registro não pode ser deletado!");
                    }
                    //Logger.getLogger(InfoBanco.class.getName()).log(Level.SEVERE, "alterar", e);
                }
            }
            oConn.setAutoCommit(true);
        }catch(SQLException ex) {
            Logger.getLogger(InfoBanco.class.getName()).log(Level.SEVERE, "alterar", ex);
        }
    }
    
    public List<Object> queryList(String sql,Object oEntidade) {
        List<Object> lista = new LinkedList<>();
        List<String> nomesAtributos = new LinkedList<>();
        Class<? extends Object> c = oEntidade.getClass();
        if(oConn!=null) {
            oStm = null;
            try {
                oConn.setAutoCommit(false);
                oStm = oConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                oSet = oStm.executeQuery(sql);

                for(Field f: c.getDeclaredFields()) {
                    f.setAccessible(true);
                    Coluna col = f.getAnnotation(Coluna.class);
                    if(col!=null) {
                        nomesAtributos.add(!col.nomeColuna().equals("") ? col.nomeColuna() : f.getName());
                    } else {
                        nomesAtributos.add(f.getName());
                    }
                }
                
                while(oSet.next()) {
                    for(int i=0; i<nomesAtributos.size(); i++) {
                        lista.add(oSet.getObject(nomesAtributos.get(i)));
                    }
                }

            }catch(SQLException e) {
                try {
                    oConn.rollback();
                } catch (SQLException ex) {
                    Logger.getLogger(InfoBanco.class.getName()).log(Level.SEVERE, "alterar", ex);
                }
                Logger.getLogger(InfoBanco.class.getName()).log(Level.SEVERE, "alterar", e);
            } catch (SecurityException | IllegalArgumentException ex) {
                Logger.getLogger(SimilarToPersistence.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return lista;
    }
    
    public List<List<Object>> consultar(Object oEntidade,String sql) {
        Class<? extends Object> entidade = oEntidade.getClass();
        List<String> nomesAtributos = new LinkedList<>();
        List<Object> listRetorno = new LinkedList<>();
        try {
            if(oConn!=null) {
                oStm = null;
                oSet = null;
                try {
                    oConn.setAutoCommit(false);
                    oStm = oConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    oSet = oStm.executeQuery(sql);
                    oConn.commit();
                    for(Field f: entidade.getDeclaredFields()) {
                        f.setAccessible(true);
                        Coluna col = f.getAnnotation(Coluna.class);
                        if(col!=null) {
                            nomesAtributos.add(!col.nomeColuna().equals("") ? col.nomeColuna() : f.getName());
                        } else {
                            nomesAtributos.add(f.getName());
                        }
                    }
                
                    while(oSet.next()) {
                        for(int i=0; i<nomesAtributos.size(); i++) {
                            listRetorno.add(oSet.getObject(nomesAtributos.get(i)));
                        }
                    }
                }catch(SQLException ex) {
                    try {
                        oConn.rollback();
                    } catch (SQLException e) {
                        Logger.getLogger(InfoBanco.class.getName()).log(Level.SEVERE, "consultar", e);
                    }
                    Logger.getLogger(InfoBanco.class.getName()).log(Level.SEVERE, "consultar", ex);
                }
            }
            oConn.setAutoCommit(true);
        }catch(SQLException ex) {
            Logger.getLogger(InfoBanco.class.getName()).log(Level.SEVERE, "consultar", ex);
        }
        return Arrays.asList(listRetorno);
    }
    
}