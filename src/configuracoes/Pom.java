
package configuracoes;

/**
 *
 * @author hallef.sud
 */
public enum Pom {
    
    SQLMSG("true"),//jdbc:mariadb://localhost:3306/frame
    URL("jdbc:postgresql://localhost:5434/frame"),//jdbc:mariadb://localhost:3306/db   //jdbc:postgresql://localhost:5434/frame
    USER("postgres"),
    PASSWORD("5432");
    
    private final String VALUE;
    
    private Pom(String v) {
        VALUE = v;
    }
    
    public String getValue() {
        return VALUE;
    }
}
