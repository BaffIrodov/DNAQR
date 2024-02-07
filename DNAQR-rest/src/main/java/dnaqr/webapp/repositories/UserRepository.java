package dnaqr.webapp.repositories;


import dnaqr.webapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByLogin(String login);
    User findByRole(String role);
    User findByEmail(String email);
    List<User> findAllByRole(String role);
}
