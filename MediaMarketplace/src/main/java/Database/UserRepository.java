package Database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import DataStructures.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    //List<User> findByFirstName(@Param("firstName") String firstName);
}
