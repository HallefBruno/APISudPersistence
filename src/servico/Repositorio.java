
package servico;

import anotacoes.Coluna;
import anotacoes.Entidade;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import util.Excecao;
import util.InfoBanco;

/**
 *
 * @author hallef.sud
 */
public class Repositorio {

    private final InfoBanco oEInfoBanco = new InfoBanco();
    private List<String> listaDeFielsAnotados = new LinkedList<>();
    private List<String> listaDeColunasDaTabelaEntidade = new LinkedList<>();
    private List<Object> listaDevaloresAtributos = new LinkedList<>();
    private List<Object> listaDevaloresAtributosConsulta = new LinkedList<>();
    private String retornSql = "";
    private String nomeDoSchema = "";
    private String nomeDaEntidade = "";
    public static boolean sqlMensagem;
    
    private String getNomeEntidade(Object oEntidade) {
        Class<? extends Object> entidadeAnotada = oEntidade.getClass();
        Entidade nome = entidadeAnotada.getAnnotation(Entidade.class);
        nomeDaEntidade = "";
        if(nome!=null) {
            String nomeCasoSejaVazio = entidadeAnotada.getSimpleName();
            if(nome.nomeTabela().isEmpty()) {
                nomeDaEntidade = nomeCasoSejaVazio.toLowerCase();
                return nomeDaEntidade;
            }
            nomeDaEntidade = nome.nomeTabela();
            return nomeDaEntidade;
        }
        throw new Excecao(Repositorio.class,"Entidade nao foi anotada "+oEntidade.getClass().getSimpleName(),null);
    }
    
    //Uso este para retornar o nome da entidade evitando que o metodo
    //getNomeEntidade(Object oEntidade) seja acionado varias vezes
    private String getNomeDaEntidade() {
        return nomeDaEntidade;
    }
    
    private String getSchemaEntidade(Object oEntidade) {
        Class<? extends Object> entidadeAnotada = oEntidade.getClass();
        Entidade anotationEntidade = entidadeAnotada.getAnnotation(Entidade.class);
        nomeDoSchema = "";
        if(getNomeDaEntidade()!=null && !getNomeDaEntidade().isEmpty()) {
            boolean possuiSchema = oEInfoBanco.validaSchema(anotationEntidade.tabelaSchema(),getNomeDaEntidade());
            String nomeSchema = oEInfoBanco.getNameSchema();
            if(possuiSchema) {
                nomeDoSchema = anotationEntidade.tabelaSchema().isEmpty() ? nomeSchema : anotationEntidade.tabelaSchema();
                return nomeDoSchema;
            } else {
                throw new Excecao(" Schema inexistente "+oEntidade.getClass().getName()+" "+anotationEntidade.tabelaSchema());
            }
        }
        
        throw new Excecao(Repositorio.class,"Entidade nao foi anotada "+oEntidade.getClass().getName(),null);
        
    }
    
    //Uso este metodo para retornar o nome do schema
    //para nao realizar conexoes quando o metodo oEInfoBanco.validaSchema
    //receber uma msg.
    private String getNomeDoSchema() {
        return nomeDoSchema;
    }

    
    private List<String> varrerFieldsAnotados(Object oEntidade) {
        List<String> listaColunas = new ArrayList<>();
        listaDevaloresAtributos = new LinkedList<>();
        Long idSeq;
        String nomeSchema;
        String nomeEntidade;
        Field[] fields = getFieldsAnotados(oEntidade.getClass());
        for (Field field : fields) {
            field.setAccessible(true);
            Coluna col = field.getAnnotation(Coluna.class);
            try {
                listaDevaloresAtributosConsulta.add(field.get(oEntidade));
                if (col != null) {
                    listaColunas.add(!col.nomeColuna().equals("") ? col.nomeColuna() : field.getName());

                    if (field.get(oEntidade) == null) {
                        listaDevaloresAtributos.add(null);
                    } else {
                        if (field.get(oEntidade).toString().contains("@")) {
                            nomeEntidade = getNomeEntidade(field.get(oEntidade));
                            nomeSchema = getSchemaEntidade(field.get(oEntidade));
                            idSeq = oEInfoBanco.getIdSequenceTable(nomeSchema, nomeEntidade);
                            nomeDaEntidade = getNomeEntidade(oEntidade);
                            nomeDoSchema = getSchemaEntidade(oEntidade);
                            listaDevaloresAtributos.add(idSeq);
                        } else {
                            listaDevaloresAtributos.add(field.get(oEntidade));
                        }
                    }
                }

            } catch (IllegalAccessException e) {
                throw new Excecao(Repositorio.class, "metodo varrerFieldsAnotados" + oEntidade.getClass().getSimpleName(), e);
            }
        }
        if (listaColunas.isEmpty()) {
            throw new Excecao(Repositorio.class, "Nenhum atributo anotado da entidade: " + oEntidade.getClass().getSimpleName(), null);
        }
        return listaColunas;
    }
    
    private boolean compararListaDeAtributosComListaColunas(Object oEntidade) {
        listaDeFielsAnotados = varrerFieldsAnotados(oEntidade);
        listaDeColunasDaTabelaEntidade = oEInfoBanco.getColumns(getNomeDoSchema(), getNomeDaEntidade());
        int qtdEntrouNoIf = 0;
        if(listaDeFielsAnotados.size() == listaDeColunasDaTabelaEntidade.size()) {
            for(int i=0; i<listaDeFielsAnotados.size(); i++) {
                if(listaDeFielsAnotados.get(i).equals(listaDeColunasDaTabelaEntidade.get(i))) {
                    qtdEntrouNoIf++;
                } else {
                    throw new Excecao(Repositorio.class,"Anotacao incorreta, entidade: "+getNomeDaEntidade()+listaDeFielsAnotados+" tabela:"+listaDeColunasDaTabelaEntidade+" "+oEntidade.getClass().getSimpleName(),null);
                }
            }
        } else {
            
            throw new Excecao(Repositorio.class,
            "Quantidade de atributos da entidade com as da tabela estao diferentes\n ou a tabela nao possui chave primaria "
            +listaDeColunasDaTabelaEntidade+" "+listaDeFielsAnotados, null);
        }
        return qtdEntrouNoIf == listaDeFielsAnotados.size();
    }
    
    private String sqlInsert() {
        String tb = "";
        String at = "";
        retornSql = "";
        StringBuilder sql = new StringBuilder();
        
        for(int i=0; i<listaDeFielsAnotados.size(); i++) {
            tb = tb +"'"+listaDevaloresAtributos.get(i)+"'"+",";//+ listaDeColunasDaTabelaEntidade.get(i)+"="+
            at = at + listaDeFielsAnotados.get(i)+",";
        }
        sql.append("INSERT INTO ").append(getNomeDoSchema()).append(".").append(getNomeDaEntidade()).append("(")
           .append(at.substring(0,at.length()-1)).append(")").append(" VALUES (").append(tb.substring(0,tb.length()-1)).append(")");

        retornSql = sql.toString();
        
        //Print msg sql
        if(sqlMensagem)
            System.err.println(retornSql);
        
        return retornSql;
    }
    
    private String sqlUpdate(Long id) {
        String tb = "";
        String at = "";
        retornSql = "";
        StringBuilder sql = new StringBuilder();
        
        for(int i=0; i<listaDeFielsAnotados.size(); i++) {
            tb = tb + listaDeColunasDaTabelaEntidade.get(i)+"=" +"'"+listaDevaloresAtributos.get(i)+"'"+",";//+ listaDeColunasDaTabelaEntidade.get(i)+"="+
            at = at + listaDeFielsAnotados.get(i)+",";
        }
        
        sql.append("UPDATE ").append(getNomeDoSchema()).append(".").append(getNomeDaEntidade()).append(" SET ").append(tb.substring(0,tb.length()-1))
           .append(" WHERE ").append(oEInfoBanco.getPrimaryKey(getNomeDoSchema(), getNomeDaEntidade())).append(" = ").append(id);
        
        retornSql = sql.toString();
        
        //Print msg sql
        if(sqlMensagem)
            System.err.println(retornSql);
        
        return retornSql;
    }
    
    private String sqlDelete(Long id) {
        retornSql = "";
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(getNomeDoSchema()).append(".").append(getNomeDaEntidade())
           .append(" WHERE ").append(oEInfoBanco.getPrimaryKey(getNomeDoSchema(), getNomeDaEntidade())).append(" = ").append(id);
        retornSql = sql.toString();
        
        //Print msg sql
        if(sqlMensagem)
            System.err.println(retornSql);
        
        return retornSql;
    }
    
    private String sqlConsult(Object oEntidade) {
        String tb = "";
        retornSql = "";
        StringBuilder sql = new StringBuilder();
        listaDeColunasDaTabelaEntidade = oEInfoBanco.getColumnsAll(getNomeDoSchema(), getNomeDaEntidade());
        for(int i=0; i<listaDevaloresAtributosConsulta.size(); i++) {
            tb = tb + listaDeColunasDaTabelaEntidade.get(i)+"=" +"'"+listaDevaloresAtributosConsulta.get(i)+"'"+" OR ";
        }
        sql.append("SELECT * FROM ").append(getNomeDoSchema()).append(".").append(getNomeDaEntidade()).append(" WHERE ").append(tb.substring(0,tb.lastIndexOf(" OR ")));
        retornSql = sql.toString();
        
        //Print msg sql
        if(sqlMensagem)
            System.err.println(retornSql);
        
        return retornSql;
    }

    private Field[] getFieldsAnotados(Class<?> c) {
        if (c.getSuperclass() != null) {
            Field[] superClassFields = getFieldsAnotados(c.getSuperclass());
            Field[] thisFields = c.getDeclaredFields();
            Field[] allFields = new Field[superClassFields.length + thisFields.length];
            System.arraycopy(superClassFields, 0,allFields, 0, superClassFields.length);
            System.arraycopy(thisFields, 0, allFields,superClassFields.length, thisFields.length);
            return allFields;
        } else {
            return c.getDeclaredFields();
        }
    }
    
    public boolean persist(Object oEntidade) {
        if (getNomeEntidade(oEntidade) != null && !getNomeDaEntidade().isEmpty()
                && getSchemaEntidade(oEntidade) != null && !getNomeDoSchema().isEmpty()
                && compararListaDeAtributosComListaColunas(oEntidade)) {
            sqlInsert();
            return true;
        }
        return false;
    }

    public boolean update(Object oEntidade, Long id) {
        if (getNomeEntidade(oEntidade) != null && !getNomeDaEntidade().isEmpty()
                && getSchemaEntidade(oEntidade) != null && !getNomeDoSchema().isEmpty()
                && compararListaDeAtributosComListaColunas(oEntidade)) {
            sqlUpdate(id);
            return true;
        }
        return false;
    }
    
    public boolean delete(Object oEntidade, Long id) {
        if (getNomeEntidade(oEntidade) != null && !getNomeDaEntidade().isEmpty()
                && getSchemaEntidade(oEntidade) != null && !getNomeDoSchema().isEmpty()
                && compararListaDeAtributosComListaColunas(oEntidade)) {
            sqlDelete(id);
            return true;
        }
        return false;
    }
    
    public boolean consult(Object oEntidade) {
        if (getNomeEntidade(oEntidade) != null && !getNomeDaEntidade().isEmpty()
                && getSchemaEntidade(oEntidade) != null && !getNomeDoSchema().isEmpty()
                && compararListaDeAtributosComListaColunas(oEntidade)) {
            sqlConsult(oEntidade);
            return true;
        }
        return false;
    }
    
    public String retornaSql() {
        return this.retornSql;
    }
}
