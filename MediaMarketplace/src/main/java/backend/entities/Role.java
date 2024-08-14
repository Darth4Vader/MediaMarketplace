package backend.entities;

import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="roles")
public class Role implements GrantedAuthority {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, name = "authority")
    private RoleType roleType;
    
    public Role(){
    }

    public Role(RoleType roleType) {
        this.roleType = roleType;
    }

    @Override
    public String getAuthority() {
        return this.roleType.toString();
    }

    public void setAuthority(RoleType roleType){
        this.roleType = roleType;
    }

    public Long getRoleId() {
        return this.id;
    }

    public void setRoleId(Long id){
        this.id = id;
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if(!(obj instanceof Role))
			return false;
		Role other = (Role) obj;
		System.out.println(Objects.equals(roleType, other.roleType) + " " + Objects.equals(id, other.id));
		return Objects.equals(roleType, other.roleType) && Objects.equals(id, other.id);
	}
}