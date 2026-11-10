package autobaterias.demo.mapeadores;

import autobaterias.demo.dtos.usuario.UsuarioActualizarDto;
import autobaterias.demo.dtos.usuario.UsuarioCrearDto;
import autobaterias.demo.dtos.usuario.UsuarioRespuestaDto;
import autobaterias.demo.entidades.Rol;
import autobaterias.demo.entidades.Usuario;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapeador {

    public UsuarioRespuestaDto aRespuesta(Usuario usuario) {
        return new UsuarioRespuestaDto(
                usuario.getId(),
                usuario.getIdentificacion(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getTelefono(),
                usuario.getCorreo(),
                usuario.getRol().getNombre(),
                usuario.getActivo());
    }

    public Usuario aEntidad(UsuarioCrearDto dto, Rol rol, String contrasenaCifrada) {
        return Usuario.builder()
                .identificacion(dto.identificacion().trim())
                .nombre(dto.nombre().trim())
                .apellido(dto.apellido().trim())
                .telefono(dto.telefono().trim())
                .correo(normalizarCorreo(dto.correo()))
                .rol(rol)
                .contrasena(contrasenaCifrada)
                .build();
    }

    public void actualizarEntidad(Usuario usuario, UsuarioActualizarDto dto, Rol rol) {
        usuario.setIdentificacion(dto.identificacion().trim());
        usuario.setNombre(dto.nombre().trim());
        usuario.setApellido(dto.apellido().trim());
        usuario.setTelefono(dto.telefono().trim());
        usuario.setCorreo(normalizarCorreo(dto.correo()));
        usuario.setRol(rol);
    }

    public String normalizarCorreo(String correo) {
        return correo.trim().toLowerCase(Locale.ROOT);
    }
}
