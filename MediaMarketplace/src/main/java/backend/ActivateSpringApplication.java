package backend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import backend.entities.Role;
import backend.entities.RoleType;
import backend.repositories.RoleRepository;

//@Configuration
@PropertySource(value = "application.properties")
@ComponentScan({"backend", "frontend"})
@EntityScan("backend.entities") 
@EnableJpaRepositories(basePackages = "backend.repositories")
@EnableJpaAuditing
@SpringBootApplication
public class ActivateSpringApplication implements CommandLineRunner {
	@Autowired
	private RoleRepository roleRepository;
	
	
	public static ConfigurableApplicationContext create(String... args) {
		return SpringApplication.run(ActivateSpringApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		RoleType[] roleTypes = RoleType.values();
		for(RoleType roleType : roleTypes) {
			if(!roleRepository.findByRoleType(roleType).isPresent())
				roleRepository.save(new Role(roleType));
		}
	}

}
