package autobaterias.demo.mapeadores;

import autobaterias.demo.dtos.rol.RolRespuestaDto;
import autobaterias.demo.dtos.rol.RolSolicitudDto;
import autobaterias.demo.entidades.Rol;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class RolMapeador {

    public RolRespuestaDto aRespuesta(Rol rol) {
        return new RolRespuestaDto(rol.getId(), rol.getNombre());
    }

    public Rol aEntidad(RolSolicitudDto dto) {
        return Rol.builder().nombre(normalizarNombre(dto.nombre())).build();
    }

    public void actualizarEntidad(Rol rol, RolSolicitudDto dto) {
        rol.setNombre(normalizarNombre(dto.nombre()));
    }

    public String normalizarNombre(String nombre) {
        return nombre.trim().toUpperCase(Locale.ROOT);
    }
}
