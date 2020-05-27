package Security;

/**
 * @author Doria
 */
public interface IIdentity {

    /**
     *
     * @return
     */
    public boolean isLoggendIn();

    /**
     *
     * @return
     */
    public String getUsername();

    /**
     *
     * @return
     */
    public String getPassword();
}
