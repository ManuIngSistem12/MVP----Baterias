package autobaterias.demo.servicios;

import autobaterias.demo.dtos.bateria.BateriaRespuestaDto;
import autobaterias.demo.dtos.bateria.BateriaSolicitudDto;
import java.util.List;

public interface IBateriaServicio {

    List<BateriaRespuestaDto> listarTodos();

    BateriaRespuestaDto buscarPorId(Integer id);

    BateriaRespuestaDto guardar(BateriaSolicitudDto dto);

    BateriaRespuestaDto actualizar(Integer id, BateriaSolicitudDto dto);

    void eliminar(Integer id);
}
