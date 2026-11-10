package autobaterias.demo.servicios;

import autobaterias.demo.dtos.usuario.UsuarioActualizarDto;
import autobaterias.demo.dtos.usuario.UsuarioCrearDto;
import autobaterias.demo.dtos.usuario.UsuarioRespuestaDto;
import java.util.List;

public interface IUsuarioServicio {

    List<UsuarioRespuestaDto> listarTodos();

    UsuarioRespuestaDto buscarPorId(Integer id);

    UsuarioRespuestaDto guardar(UsuarioCrearDto dto);

    UsuarioRespuestaDto actualizar(Integer id, UsuarioActualizarDto dto);

    void eliminar(Integer id);
}
