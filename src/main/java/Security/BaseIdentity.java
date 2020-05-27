package Security;

/**
 * @author Doria
 */

public abstract class BaseIdentity implements IIdentity {

    private final String username;
    private final String password;

    /**
     *
     * @param username
     * @param password
     */
    public BaseIdentity(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     *
     * @return
     */
   @Override
    public String getUsername() {
        return username;
    }

    /**
     *
     * @return
     */
    @Override
    public String getPassword() {
        return password;
    }
}

