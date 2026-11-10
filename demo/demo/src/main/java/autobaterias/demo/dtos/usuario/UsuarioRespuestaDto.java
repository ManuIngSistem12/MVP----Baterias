package autobaterias.demo.dtos.usuario;

public record UsuarioRespuestaDto(
        Integer id,
        String identificacion,
        String nombre,
        String apellido,
        String telefono,
        String correo,
        String nombreRol,
        Boolean activo
) {
}
