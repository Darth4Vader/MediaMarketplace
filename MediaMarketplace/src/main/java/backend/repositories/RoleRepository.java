package backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.entities.Role;
import backend.entities.RoleType;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	//Optional<Role> findByAuthority(String authority);
	
	Optional<Role> findByRoleType(RoleType roleType);
}
