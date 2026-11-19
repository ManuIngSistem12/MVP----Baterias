package autobaterias.demo.servicios.Impl;

import autobaterias.demo.dtos.bateria.BateriaRespuestaDto;
import autobaterias.demo.dtos.bateria.BateriaSolicitudDto;
import autobaterias.demo.entidades.Bateria;
import autobaterias.demo.entidades.Marca;
import autobaterias.demo.excepciones.ConflictoExcepcion;
import autobaterias.demo.excepciones.RecursoNoEncontradoExcepcion;
import autobaterias.demo.mapeadores.BateriaMapeador;
import autobaterias.demo.repositorios.IBateriaRepositorio;
import autobaterias.demo.repositorios.IMarcaRepositorio;
import autobaterias.demo.servicios.IBateriaServicio;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BateriaServicioImpl implements IBateriaServicio {

    private static final String RECURSO = "Batería";
    private static final String RECURSO_MARCA = "Marca";

    private final IBateriaRepositorio bateriaRepositorio;
    private final IMarcaRepositorio marcaRepositorio;
    private final BateriaMapeador bateriaMapeador;

    @Override
    public List<BateriaRespuestaDto> listarTodos() {
        return bateriaRepositorio.findAll().stream()
                .map(bateriaMapeador::aRespuesta)
                .toList();
    }

    @Override
    public BateriaRespuestaDto buscarPorId(Integer id) {
        return bateriaMapeador.aRespuesta(obtenerBateria(id));
    }

    @Override
    @Transactional
    public BateriaRespuestaDto guardar(BateriaSolicitudDto dto) {
        String codigo = bateriaMapeador.normalizarCodigo(dto.codigo());
        if (bateriaRepositorio.existsByCodigo(codigo)) {
            throw new ConflictoExcepcion("Ya existe una batería con el código '%s'".formatted(codigo));
        }
        Bateria bateria = bateriaMapeador.aEntidad(dto, obtenerMarca(dto.marcaId()));
        return bateriaMapeador.aRespuesta(bateriaRepositorio.save(bateria));
    }

    @Override
    @Transactional
    public BateriaRespuestaDto actualizar(Integer id, BateriaSolicitudDto dto) {
        Bateria bateria = obtenerBateria(id);
        String codigo = bateriaMapeador.normalizarCodigo(dto.codigo());
        if (bateriaRepositorio.existsByCodigoAndIdNot(codigo, id)) {
            throw new ConflictoExcepcion("Ya existe una batería con el código '%s'".formatted(codigo));
        }
        bateriaMapeador.actualizarEntidad(bateria, dto, obtenerMarca(dto.marcaId()));
        return bateriaMapeador.aRespuesta(bateria);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        obtenerBateria(id).setActivo(false);
    }

    private Bateria obtenerBateria(Integer id) {
        return bateriaRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion(RECURSO, id));
    }

    private Marca obtenerMarca(Integer marcaId) {
        return marcaRepositorio.findById(marcaId)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion(RECURSO_MARCA, marcaId));
    }
}
