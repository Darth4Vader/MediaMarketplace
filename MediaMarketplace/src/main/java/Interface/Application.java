package Interface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import backend.controllers.UserController;

//@Configuration
@PropertySource(value = "application.properties")
@ComponentScan({"backend"})
@EntityScan("backend.entities") 
@EnableJpaRepositories(basePackages = "backend.repositories")
@SpringBootApplication
public class Application implements CommandLineRunner {
	
	@Autowired
	private UserController userController;
	
	
	  public static void main(String... args) {
		    SpringApplication.run(Application.class, args);
		    //System.out.println(UserController);
	  }

	@Override
	public void run(String... args) throws Exception {
		System.out.println(userController.getUserById(1l).getUserName());
		// TODO Auto-generated method stub
		
	}
	  
	  /*@Bean
	  ApplicationRunner applicationRunner(Environment environment) {
		  /*return args ->
		  	log.info*/
	  //}

}
