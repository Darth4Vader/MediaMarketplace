package backend.entities;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

/**
 * Represents a user in the system, implementing {@link UserDetails} for Spring Security integration.
 * <p>
 * The {@code User} entity stores user credentials, personal information, and roles. It integrates with Spring Security
 * to manage authentication and authorization.
 * </p>
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {

    /**
     * Serial version UID for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Username for the user, must be unique.
     */
    @Column(name = "user_name", nullable = false, unique = true)
    private String username;

    /**
     * Encrypted password for the user.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Optional name of the user.
     */
    private String name;

    /**
     * Roles associated with the user, used for authorization.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    private Set<Role> authorities;

    /**
     * Default constructor for JPA.
     */
    public User() {
        this.authorities = new HashSet<>();
    }

    /**
     * Constructs a {@code User} with the specified username and password, and initializes an empty set of authorities.
     * 
     * @param userName the username of the user
     * @param password the password of the user
     */
    public User(String userName, String password) {
        this(userName, password, new HashSet<>());
    }

    /**
     * Constructs a {@code User} with the specified username, password, and authorities.
     * 
     * @param userName the username of the user
     * @param password the password of the user
     * @param authorities the roles associated with the user
     */
    public User(String userName, String password, Set<Role> authorities) {
        this.username = userName;
        this.password = password;
        this.authorities = authorities;
    }

    /**
     * Returns the authorities granted to the user.
     * <p>
     * This method is part of the {@link UserDetails} interface and returns the roles assigned to the user.
     * </p>
     * 
     * @return a collection of granted authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    /**
     * Returns the password of the user.
     * <p>
     * This method is part of the {@link UserDetails} interface and returns the user's password.
     * </p>
     * 
     * @return the password
     */
    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * Returns the username of the user.
     * <p>
     * This method is part of the {@link UserDetails} interface and returns the user's username.
     * </p>
     * 
     * @return the username
     */
    @Override
    public String getUsername() {
        return this.username;
    }

    /**
     * Returns the unique identifier of the user.
     * 
     * @return the user ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the password for the user.
     * 
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the name of the user.
     * 
     * @return the user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user.
     * 
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}