package autobaterias.demo.repositories;

import autobaterias.demo.entities.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Optional<User> findByIdentity(String identity);

    boolean existsByEmail(String email);

    boolean existsByIdentity(String identity);
}
