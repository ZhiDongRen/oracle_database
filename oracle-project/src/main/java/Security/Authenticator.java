/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Security;
import Model.Reload;
import Application.InvalidCredentialsException;


public class Authenticator {

    private IIdentity identity;

    public Authenticator() {
        this.identity = null;
    }

    /**
     *
     * @return
     */
    public IIdentity getIdentity() {
        if (identity == null) {
         
        }
        return identity;
    }

    /**
     *
     * @param username
     * @param password
     * @return
     * @throws InvalidCredentialsException
     */
   
    public IIdentity login(String username, String password) throws InvalidCredentialsException {

        identity = new Identity(username, password);
        if (!Reload.isConnectionValid()) {
            logout();
            throw new InvalidCredentialsException("Invalid username or password.");
        }
        return getIdentity();
    }

    /**
     *
     */
 public void logout() {
       identity = null;
}
}
