
package configuracoes;

/**
 *
 * @author hallef.sud
 */
public enum Pom {
    
    SQLMSG("true"),
    URL("jdbc:mariadb://localhost:3306/frame"),//jdbc:mariadb://localhost:3306/db
    USER("root"),
    PASSWORD("5432");
    
    private final String VALUE;
    
    private Pom(String v) {
        VALUE = v;
    }
    
    public String getValue() {
        return VALUE;
    }
}
