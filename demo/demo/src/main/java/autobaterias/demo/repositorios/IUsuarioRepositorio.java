package autobaterias.demo.repositorios;

import autobaterias.demo.entidades.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUsuarioRepositorio extends JpaRepository<Usuario, Integer> {

    @Override
    @EntityGraph(attributePaths = "rol")
    List<Usuario> findAll();

    @Override
    @EntityGraph(attributePaths = "rol")
    Optional<Usuario> findById(Integer id);

    Optional<Usuario> findByCorreo(String correo);

    Optional<Usuario> findByIdentificacion(String identificacion);

    boolean existsByCorreo(String correo);

    boolean existsByIdentificacion(String identificacion);

    boolean existsByCorreoAndIdNot(String correo, Integer id);

    boolean existsByIdentificacionAndIdNot(String identificacion, Integer id);

    boolean existsByRolId(Integer rolId);
}
