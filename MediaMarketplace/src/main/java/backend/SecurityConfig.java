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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig /*extends WebSecurityConfiguration*/ {

	/*private PersistentTokenRepository persistenceTokenRepository;
	
	@Autowired
	private UserDetailsService userDetailsService;*/
	
    private final RSAKeysPair rsaKeys;

    public SecurityConfig(RSAKeysPair rsaKeys){
        this.rsaKeys = rsaKeys;
    }
	
    @Bean
    public AuthenticationManager authManager(UserDetailsService detailsService){
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(detailsService);
        daoProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(daoProvider);
    }
    
    
    /**
     * Ignoring the access deny exception, so that the user will get it and can handle it's exception
     * @return
     */
    @Bean
    public SimpleMappingExceptionResolver exceptionResolver() {
        SimpleMappingExceptionResolver exceptionResolver = new SimpleMappingExceptionResolver();
        
        Properties exceptionMappings = new Properties();

       // exceptionMappings.put("java.lang.Exception", "error/error");
       // exceptionMappings.put("java.lang.RuntimeException", "error/error");
        
        //exceptionMappings.put("excludedExceptions", "org.springframework.security.access.AccessDeniedException");
        //exceptionMappings.put("excludedExceptions", "org.springframework.security.authorization.AuthorizationDeniedException");
        //exceptionMappings.put("defaultErrorView", "uncaughtException");

        
        exceptionMappings.put("org.springframework.security.AccessDeniedException", "accessDenied");
        
        //exceptionResolver.pr
        
        exceptionResolver.setExceptionMappings(exceptionMappings);

       exceptionResolver.setExcludedExceptions(AuthorizationDeniedException.class, AccessDeniedException.class);
        
        exceptionResolver.setDefaultErrorView("uncaughtException");

        return exceptionResolver;
    }
    
    
    /**
     * Used for Swaager web gui
     * delete when using javafx
     */
    private static final String[] AUTH_WHITELIST = {

            // for Swagger UI v2
            "/v2/api-docs",
            "/swagger-ui.html",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/webjars/**",

            // for Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	http
    		.csrf(csrf -> csrf.disable()) // Disables CSRF protection, common in stateless REST APIs.
        	.authorizeHttpRequests(requests -> requests
        		
        		//This is for swagger, remove when using javafx
        		.requestMatchers(AUTH_WHITELIST).permitAll()
        		
        		
        		
        		.requestMatchers("/auth/**").permitAll()
        		//.requestMatchers("/login").permitAll()
        			
                .requestMatchers("/market").permitAll()
                //.requestMatchers("/market/movies").hasRole("ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasAnyRole("ADMIN", "USER")
                
                
                .anyRequest().authenticated()
    		)
        	.formLogin(Customizer.withDefaults());
    		/*.rememberMe(c -> c
                    .tokenRepository(persistenceTokenRepository)
                    .rememberMeCookieName("rememberme")
                    .tokenValiditySeconds(60 * 60 * 24) 
                    .alwaysRemember(true)
                    .useSecureCookie(true)
    		);*/
            /*.formLogin(formLogin -> formLogin
	            //.loginPage("/login.html")
	            .loginProcessingUrl("/login")
	            //.successHandler(myAuthenticationSuccessHandler()));
	            );
	            //.permitAll());
        	/*.formLogin(formLogin -> formLogin
        		.loginProcessingUrl("/api/login")
        		.permitAll()
			)
        	.logout(formLogout -> formLogout
                .logoutUrl("/api/logout")
                .permitAll()
        	)*/
    		//.rememberMe(Customizer.withDefaults());
    	
        http.oauth2ResourceServer(oauth2 -> oauth2
        		.jwt(jwt -> jwt
        		//.decoder(jwtDecoder())
        		.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        
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
        		.defaultAuthenticationEntryPointFor((request, response, accessDeniedException) -> {
        			throw new RuntimeException(accessDeniedException);
        		}, null)
        		
        );
    	return http.build();
    }
    
    /*@Bean
    public PersistentTokenBasedRememberMeServices getPersistentTokenBasedRememberMeServices() {
        PersistentTokenBasedRememberMeServices persistenceTokenBasedservice = new PersistentTokenBasedRememberMeServices("rememberme", userDetailsService, persistenceTokenRepository);
        persistenceTokenBasedservice.setAlwaysRemember(true);
        return persistenceTokenBasedservice;
      }*/
    
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtConverter;
    }
    
    @Bean
    public JwtDecoder jwtDecoder(){
        return NimbusJwtDecoder.withPublicKey(rsaKeys.getPublicKey()).build();
    }

    @Bean
    public JwtEncoder jwtEncoder(){
        JWK jwk = new RSAKey.Builder(rsaKeys.getPublicKey()).privateKey(rsaKeys.getPrivateKey()).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }
}
