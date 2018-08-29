
package contrato;

import java.util.List;

/**
 *
 * @author hallef.sud
 */
public interface InfoBancoService {
    public String getPrimaryKey(String schema, String nomeTabela);
    public String getForeignKey(String schema, String nomeTabela);
    public List getColumnsTabela(String schema, String nomeTabela);
}
