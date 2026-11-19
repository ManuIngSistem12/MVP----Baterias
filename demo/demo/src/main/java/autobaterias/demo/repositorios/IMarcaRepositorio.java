package autobaterias.demo.repositorios;

import autobaterias.demo.entidades.Marca;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMarcaRepositorio extends JpaRepository<Marca, Integer> {

    Optional<Marca> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);
}
