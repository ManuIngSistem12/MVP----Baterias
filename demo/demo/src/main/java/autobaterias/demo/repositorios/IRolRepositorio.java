package autobaterias.demo.repositorios;

import autobaterias.demo.entidades.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRolRepositorio extends JpaRepository<Rol, Integer> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Integer id);
}
