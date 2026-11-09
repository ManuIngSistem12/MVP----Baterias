package autobaterias.demo.dtos.rol;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RolSolicitudDto(
        @NotBlank @Size(max = 50) String nombre
) {
}
