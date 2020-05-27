package Security;

/**
 * @author Doria
 */
public class Identity extends BaseIdentity implements IIdentity {

    /**
     *
     * @param username
     * @param password
     */
    public Identity(String username, String password) {
        super(username, password);
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isLoggendIn() {
        return true;
    }
}
