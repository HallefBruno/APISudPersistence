package util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hallef.sud
 */
public class Excecao extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public Excecao(String msg) {
        msgErro(msg);
    }

    public Excecao(Throwable e) {
        msgErro(e);
    }

    public Excecao(Object oEntidade, String msgErro, Throwable e) {
        logs(oEntidade, msgErro, e);
    }

    private void msgErro(String msg) {
        throw new RuntimeException(msg);
    }

    private void msgErro(Throwable e) {
        throw new RuntimeException(e);
    }

    private void logs(Object oEntidade, String msgErro, Throwable e) {
        Logger.getLogger(oEntidade.getClass().getName()).log(Level.SEVERE, msgErro, e);
    }
}
