package Application;

/**
 *
 * @author doria
 */
public class InvalidCredentialsException extends Exception {

    /**
     * @param message 从右边我描述了无效登录数据的来源
     */
    public InvalidCredentialsException(String message) {
        super(message);
    }
}