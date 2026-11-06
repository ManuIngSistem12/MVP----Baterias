package autobaterias.demo.mappers;

import autobaterias.demo.dtos.rol.RolRequestDto;
import autobaterias.demo.dtos.rol.RolResponseDto;
import autobaterias.demo.entities.Rol;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class RolMapper {

    public RolResponseDto toResponse(Rol rol) {
        return new RolResponseDto(rol.getId(), rol.getName());
    }

    public Rol toEntity(RolRequestDto dto) {
        return Rol.builder().name(normalizeName(dto.name())).build();
    }

    public void updateEntity(Rol rol, RolRequestDto dto) {
        rol.setName(normalizeName(dto.name()));
    }

    public String normalizeName(String name) {
        return name.trim().toUpperCase(Locale.ROOT);
    }
}
