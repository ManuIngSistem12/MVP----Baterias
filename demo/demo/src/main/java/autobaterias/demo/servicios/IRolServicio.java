package autobaterias.demo.servicios;

import autobaterias.demo.dtos.rol.RolRespuestaDto;
import autobaterias.demo.dtos.rol.RolSolicitudDto;
import java.util.List;

public interface IRolServicio {

    List<RolRespuestaDto> listarTodos();

    RolRespuestaDto buscarPorId(Integer id);

    RolRespuestaDto guardar(RolSolicitudDto dto);

    RolRespuestaDto actualizar(Integer id, RolSolicitudDto dto);

    void eliminar(Integer id);
}
