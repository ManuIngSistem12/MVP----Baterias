package autobaterias.demo.repositorios;

import autobaterias.demo.entidades.Bateria;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IBateriaRepositorio extends JpaRepository<Bateria, Integer> {

    @Override
    @EntityGraph(attributePaths = "marca")
    List<Bateria> findAll();

    @Override
    @EntityGraph(attributePaths = "marca")
    Optional<Bateria> findById(Integer id);

    Optional<Bateria> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Integer id);

    boolean existsByMarcaId(Integer marcaId);
}
