package autobaterias.demo.servicios.Impl;

import autobaterias.demo.dtos.rol.RolRespuestaDto;
import autobaterias.demo.dtos.rol.RolSolicitudDto;
import autobaterias.demo.entidades.Rol;
import autobaterias.demo.excepciones.ConflictoExcepcion;
import autobaterias.demo.excepciones.RecursoNoEncontradoExcepcion;
import autobaterias.demo.mapeadores.RolMapeador;
import autobaterias.demo.repositorios.IRolRepositorio;
import autobaterias.demo.servicios.IRolServicio;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RolServicioImpl implements IRolServicio {

    private static final String RECURSO = "Rol";

    private final IRolRepositorio rolRepositorio;
    private final RolMapeador rolMapeador;

    @Override
    public List<RolRespuestaDto> listarTodos() {
        return rolRepositorio.findAll().stream()
                .map(rolMapeador::aRespuesta)
                .toList();
    }

    @Override
    public RolRespuestaDto buscarPorId(Integer id) {
        return rolMapeador.aRespuesta(obtenerRol(id));
    }

    @Override
    @Transactional
    public RolRespuestaDto guardar(RolSolicitudDto dto) {
        String nombre = rolMapeador.normalizarNombre(dto.nombre());
        if (rolRepositorio.existsByNombreIgnoreCase(nombre)) {
            throw new ConflictoExcepcion("El rol '%s' ya existe".formatted(nombre));
        }
        return rolMapeador.aRespuesta(rolRepositorio.save(rolMapeador.aEntidad(dto)));
    }

    @Override
    @Transactional
    public RolRespuestaDto actualizar(Integer id, RolSolicitudDto dto) {
        Rol rol = obtenerRol(id);
        String nombre = rolMapeador.normalizarNombre(dto.nombre());
        if (rolRepositorio.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ConflictoExcepcion("El rol '%s' ya existe".formatted(nombre));
        }
        rolMapeador.actualizarEntidad(rol, dto);
        return rolMapeador.aRespuesta(rol);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        rolRepositorio.delete(obtenerRol(id));
    }

    private Rol obtenerRol(Integer id) {
        return rolRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion(RECURSO, id));
    }
}
