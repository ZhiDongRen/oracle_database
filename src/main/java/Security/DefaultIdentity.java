package Security;

/**
 
 * @author Doira
 */
public class DefaultIdentity extends BaseIdentity implements IIdentity {

    /**
     * 
     * @param username
     * @param password
     */
    public DefaultIdentity(String username, String password) {
        super(username, password);
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isLoggendIn() {
        return false;
    }
}