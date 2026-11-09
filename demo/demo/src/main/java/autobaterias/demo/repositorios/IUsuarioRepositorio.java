package autobaterias.demo.repositorios;

import autobaterias.demo.entidades.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUsuarioRepositorio extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByCorreo(String correo);

    Optional<Usuario> findByIdentificacion(String identificacion);

    boolean existsByCorreo(String correo);

    boolean existsByIdentificacion(String identificacion);
}
