package backend.auth;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ADMIN')")
//@PreAuthorize("hasRole(T(backend.entities.Role).ADMIN) ")
//@PreAuthorize("hasRole(T(backend.entities.RoleType).ADMIN) ")
//        "|| hasRole(T(com.bs.dmsbox.api.constants.RoleConstants).ROLE_ADMIN)" +
//        "|| (hasRole(T(com.bs.dmsbox.api.constants.RoleConstants).ROLE_CUSTOMER) && #userId == principal.username)")
public @interface AuthenticateAdmin {
}
