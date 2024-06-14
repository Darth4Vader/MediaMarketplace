package backend.services;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import backend.entities.User;

@Service
public class TokenService {
	
    @Autowired
    private JwtEncoder jwtEncoder;
    
    @Autowired
    private JwtDecoder jwtDecoder;

    public String generateJwt(Authentication auth){

        Instant nowTime = Instant.now();

        String scope = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(nowTime)
            .subject(auth.getName())
            .claim("scope", scope)
            .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
    
    /*public Authentication getAuthentication(String token) {
    	Jwt jwt = jwtDecoder.decode(token);
    	jwt.
    	return (Authentication) jwt;
    }*/
    
    public String getCurretToken() {
    	System.out.println(SecurityContextHolder.getContext().getAuthentication());
    	return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    
    public User getCurretUser() {
    	return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    
    public String getCurrentUserName(String token) {
    	Jwt jwt = jwtDecoder.decode(token);
    	return jwt.getClaim("sub");
    	//return (User) getAuthentication(token).getPrincipal();
    	//return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    
    /*public String validateJwt(String token) {
    	Jwt auth = getJwt(token);
    }*/
    
    public void invalidateJwt(String token){
    	Jwt auth = jwtDecoder.decode(token);
    	System.out.println(auth.getClaims());
    	//auth.get
    	System.out.println(SecurityContextHolder.getContext().getAuthentication());
    	
    	
    	
    	
        /*Instant nowTime = Instant.now();

        String scope = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(nowTime)
            .subject(auth.getName())
            .claim("scope", scope)
            .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();*/
    }
	
}
