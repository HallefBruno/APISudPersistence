
package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hallef.sud
 */
public class LerArquivoPropetiesConformePath {
    
    private static Properties oProperties;
    private static FileInputStream oInputStream;
    
    private static Properties lerArquivoProperties(String path) {
        try {
            oProperties = new Properties();
            oInputStream = new FileInputStream(new File(path));
            oProperties.load(oInputStream);
        } catch (IOException ex) {
            Logger.getLogger(LerArquivoPropetiesConformePath.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                oInputStream.close();
            } catch (IOException ex) {Logger.getLogger(LerArquivoPropetiesConformePath.class.getName()).log(Level.SEVERE, null, ex);}
        }
        return oProperties;
    }
    
    public static Properties getValuesProperties(String path) {
        return lerArquivoProperties(path);
    }
}
