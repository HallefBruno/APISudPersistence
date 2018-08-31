
package util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import servico.SimilarToPersistence;

/**
 *
 * @author hallef.sud
 */
public class OrganizeList {//col[column].trim()+","+col[column].trim()+","+col[column].trim()+","+col[column].trim()+","+col[column].trim()+","+col[column].trim()
    
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
//for(int column=0; column<SimilarToPersistence.qtdColumn; column++) {
//    System.out.println(col[0].trim()+","+col[1].trim()+","+col[2].trim()+","+col[3].trim()+","+col[4].trim()+","+col[5].trim());
//}