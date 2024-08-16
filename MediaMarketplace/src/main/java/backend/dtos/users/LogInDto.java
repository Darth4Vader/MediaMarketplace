package backend.dtos.users;

/**
 * Data Transfer Object for user login information.
 */
public class LogInDto {

    /**
     * The username of the user attempting to log in.
     */
    private String userName;

    /**
     * The password of the user attempting to log in.
     */
    private String password;

    /**
     * Constructs a new {@code LogInDto} with the specified username and password.
     * 
     * @param userName the username of the user
     * @param password the password of the user
     */
    public LogInDto(String userName, String password) {
        super();
        this.userName = userName;
        this.password = password;
    }

    /**
     * Gets the username of the user attempting to log in.
     * 
     * @return the username of the user
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the username of the user attempting to log in.
     * 
     * @param userName the username of the user
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets the password of the user attempting to log in.
     * 
     * @return the password of the user
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user attempting to log in.
     * 
     * @param password the password of the user
     */
    public void setPassword(String password) {
        this.password = password;
    }
}