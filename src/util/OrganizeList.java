
package util;

import java.util.Arrays;
import java.util.List;
import servico.SimilarToPersistence;

/**
 *
 * @author hallef.sud
 */
public class OrganizeList {
    
    public static String asList(List<Object> junction) {
        
        StringBuilder add = new StringBuilder();
        String lista = Arrays.toString(Arrays.asList(junction).toArray());
        lista = lista.replace("[[", "").replace("]]", "");
        String[] col = lista.split(",");
        
        for(int row=0; row<SimilarToPersistence.qtdRow; row++) {
            add.append(col[0].trim()).append(",").append(col[1].trim()).append(",")
               .append(col[2].trim()).append(",").append(col[3].trim()).append(",")
               .append(col[4].trim()).append(",").append(col[5].trim()).append("\n");
        }
        
        return add.toString();
    }
}
