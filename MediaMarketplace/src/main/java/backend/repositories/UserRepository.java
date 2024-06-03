package backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    //List<User> findByFirstName(@Param("firstName") String firstName);
}
