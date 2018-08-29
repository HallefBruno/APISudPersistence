
package persistencia;

import java.util.List;
import servico.Repositorio;
import servico.SimilarToPersistence;


/**
 *
 * @author hallef.sud
 */
public class Persistencia {
    
    private final Repositorio oRepositorio;
    private final SimilarToPersistence oSimilarToPersistence;

    public Persistencia() {
        this.oRepositorio = new Repositorio();
        this.oSimilarToPersistence = new SimilarToPersistence();
    }
    
    public void persist(Object oEntidade) {
        if(oRepositorio.persist(oEntidade)) {
            oSimilarToPersistence.salvar(oRepositorio.retornaSql());
        }
    }
    
    public void update(Object oEntidade, Long id) {
        if(oRepositorio.update(oEntidade, id)) {
            oSimilarToPersistence.alterar(oRepositorio.retornaSql());
        }
    }
    
    public void delete(Object oEntidade, Long id) {
        if(oRepositorio.delete(oEntidade, id)) {
            oSimilarToPersistence.delete(oRepositorio.retornaSql());
        }
    }
    
    public List<List<Object>> consult(Object oEntidade) {
        if(oRepositorio.consult(oEntidade)) {
            return oSimilarToPersistence.consultar(oEntidade, oRepositorio.retornaSql());
        }
        return null;
    }
}
