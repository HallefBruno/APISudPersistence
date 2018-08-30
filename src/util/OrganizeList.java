
package util;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author hallef.sud
 */
public class OrganizeList {
    
    public static String asList(List<Object> junction) {
        String lista = Arrays.toString(Arrays.asList(junction).toArray());
        lista = lista.replace("[[", "").replace("]]", "");
        String[] col = lista.split(",");
        
        
        
        return lista;
    }
}
