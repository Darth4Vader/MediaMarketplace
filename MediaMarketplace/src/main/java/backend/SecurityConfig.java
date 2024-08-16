package backend;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import backend.auth.RSAKeysPair;
import backend.entities.enums.RoleType;

/**
 * Security configuration for the application.
 * <p>
 * Configures authentication, authorization, and exception handling for the application.
 * </p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final RSAKeysPair rsaKeys;

    /**
     * Constructor for injecting the RSA key pair.
     * 
     * @param rsaKeys the RSA key pair used for JWT encoding and decoding
     */
    public SecurityConfig(RSAKeysPair rsaKeys) {
        this.rsaKeys = rsaKeys;
    }

    /**
     * Provides the AuthenticationManager bean.
     * 
     * @param detailsService the UserDetailsService for loading user-specific data
     * @return an AuthenticationManager instance
     */
    @Bean
    public AuthenticationManager authManager(UserDetailsService detailsService) {
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(detailsService);
        daoProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(daoProvider);
    }

    /**
     * Configures exception handling for the application. 
     * Ignoring the access deny exception, so that the user will get it and can handle it's exception
     * 
     * @return a SimpleMappingExceptionResolver instance
     */
    @Bean
    public SimpleMappingExceptionResolver exceptionResolver() {
        SimpleMappingExceptionResolver exceptionResolver = new SimpleMappingExceptionResolver();
        
        Properties exceptionMappings = new Properties();
        exceptionMappings.put("org.springframework.security.AccessDeniedException", "accessDenied");
        exceptionResolver.setExceptionMappings(exceptionMappings);

        exceptionResolver.setExcludedExceptions(AuthorizationDeniedException.class, AccessDeniedException.class);
        exceptionResolver.setDefaultErrorView("uncaughtException");

        return exceptionResolver;
    }

    /**
     * Configures the security filter chain for HTTP requests.
     * 
     * @param http the HttpSecurity instance
     * @return a SecurityFilterChain instance
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disables CSRF protection, common in stateless REST APIs.
            .authorizeHttpRequests(requests -> requests
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/market").permitAll()
                .requestMatchers("/admin/**").hasRole(RoleType.ROLE_ADMIN.getRoleName())
                .requestMatchers("/user/**").hasAnyRole(RoleType.ROLE_ADMIN.getRoleName(), RoleType.ROLE_USER.getRoleName())
                .anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults());
        //add oauth2 protection for users.
        http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );
        //used for moving the AccessDeny exception to the JavaFX
        http.exceptionHandling(cust -> cust
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    throw new RuntimeException(accessDeniedException);
                })
                .authenticationEntryPoint((request, response, authException) -> {
                    throw new RuntimeException(authException);
                })
                .defaultAccessDeniedHandlerFor((request, response, accessDeniedException) -> {
                    throw new RuntimeException(accessDeniedException);
                }, null)
                .defaultAuthenticationEntryPointFor((request, response, authException) -> {
                    throw new RuntimeException(authException);
                }, null)
            );
        return http.build();
    }

    /**
     * Provides the PasswordEncoder bean.
     * 
     * @return a BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Provides the JwtAuthenticationConverter bean for converting JWTs to Authentication objects.
     * Used for checking users Roles.
     * 
     * @return a JwtAuthenticationConverter instance
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtConverter;
    }

    /**
     * Provides the JwtDecoder bean for decoding JWTs.
     * 
     * @return a JwtDecoder instance
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaKeys.getPublicKey()).build();
    }

    /**
     * Provides the JwtEncoder bean for encoding JWTs.
     * 
     * @return a JwtEncoder instance
     */
    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(rsaKeys.getPublicKey()).privateKey(rsaKeys.getPrivateKey()).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }
}
