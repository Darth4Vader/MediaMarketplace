package Interface;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

import backend.controllers.UserAuthenticateController;
import backend.controllers.UserController;
import backend.dto.users.RegisterDto;
import backend.entities.Role;
import backend.repositories.RoleRepository;
import backend.repositories.UserRepository;

//@Configuration
@PropertySource(value = "application.properties")
@ComponentScan({"backend", "frontend"})
@EntityScan("backend.entities") 
@EnableJpaRepositories(basePackages = "backend.repositories")
@SpringBootApplication
public class SApplication implements CommandLineRunner {
	
	@Autowired
	private UserController userController;
	@Autowired
	private UserAuthenticateController userAuthController;
	@Autowired
	private RoleRepository roleRepository;
	
	
	  public static ConfigurableApplicationContext create(String... args) {
		    return SpringApplication.run(SApplication.class, args);
	  }
	
	
	  public static void main(String... args) {
		    SpringApplication.run(SApplication.class, args);
		    //SpringApplication.
		    //System.out.println(UserController);
	  }

	@Override
	public void run(String... args) throws Exception {
		System.out.println("dis before");
		//userAuthController.registerUser(new RegisterDto("bilbo", "bag"));
		if(roleRepository.findByAuthority("ADMIN").isPresent()) return;
		Role adminRole = roleRepository.save(new Role("ADMIN"));
		System.out.println("dis");
		roleRepository.save(new Role("USER"));

		/*Set<Role> roles = new HashSet<>();
		roles.add(adminRole);*/
		System.out.println(userController.getUserById(1l));
	}
	  
	  /*@Bean
	  ApplicationRunner applicationRunner(Environment environment) {
		  /*return args ->
		  	log.info*/
	  //}

}
