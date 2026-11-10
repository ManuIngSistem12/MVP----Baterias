package autobaterias.demo.dtos.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UsuarioCrearDto(
        @NotBlank @Size(max = 50) String identificacion,
        @NotBlank @Size(max = 50) String nombre,
        @NotBlank @Size(max = 50) String apellido,
        @NotBlank @Size(max = 50) String telefono,
        @NotBlank @Email @Size(max = 100) String correo,
        @NotNull @Positive Integer rolId,
        @NotBlank @Size(min = 8, max = 72) String contrasena
) {
}
