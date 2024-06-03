import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import backend.services.UserService;
import backend.services.UserServiceImpl;

@Configuration
@Import({TestConfig.class})
public class ApplicationTestConfig {

    /*@Bean UserService userService() {
        return new UserServiceImpl();
    }*/

}
