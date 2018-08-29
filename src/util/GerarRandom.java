
package util;

import java.util.Random;

/**
 *
 * @author hallef.sud
 */
public class GerarRandom {
    
    public static String getChaveRandomString(int limit) {
        int leftLimit = 97; // letter 'a' //não mecher ou seja curioso
        int rightLimit = 122; // letter 'z'//não mecher ou seja curioso
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(limit);
        for (int i = 0; i < limit; i++) {
            int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString().toUpperCase();
    }
    
    public static String getChaveRandomStringAndNumber(int limit) {
        final String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < limit) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();
    }
}
