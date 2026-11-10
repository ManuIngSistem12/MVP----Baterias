package autobaterias.demo.servicios.Impl;

import autobaterias.demo.dtos.usuario.UsuarioActualizarDto;
import autobaterias.demo.dtos.usuario.UsuarioCrearDto;
import autobaterias.demo.dtos.usuario.UsuarioRespuestaDto;
import autobaterias.demo.entidades.Rol;
import autobaterias.demo.entidades.Usuario;
import autobaterias.demo.excepciones.ConflictoExcepcion;
import autobaterias.demo.excepciones.RecursoNoEncontradoExcepcion;
import autobaterias.demo.mapeadores.UsuarioMapeador;
import autobaterias.demo.repositorios.IRolRepositorio;
import autobaterias.demo.repositorios.IUsuarioRepositorio;
import autobaterias.demo.servicios.IUsuarioServicio;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioServicioImpl implements IUsuarioServicio {

    private static final String RECURSO = "Usuario";
    private static final String RECURSO_ROL = "Rol";

    private final IUsuarioRepositorio usuarioRepositorio;
    private final IRolRepositorio rolRepositorio;
    private final UsuarioMapeador usuarioMapeador;
    private final PasswordEncoder codificadorContrasena;

    @Override
    public List<UsuarioRespuestaDto> listarTodos() {
        return usuarioRepositorio.findAll().stream()
                .map(usuarioMapeador::aRespuesta)
                .toList();
    }

    @Override
    public UsuarioRespuestaDto buscarPorId(Integer id) {
        return usuarioMapeador.aRespuesta(obtenerUsuario(id));
    }

    @Override
    @Transactional
    public UsuarioRespuestaDto guardar(UsuarioCrearDto dto) {
        String correo = usuarioMapeador.normalizarCorreo(dto.correo());
        String identificacion = dto.identificacion().trim();
        if (usuarioRepositorio.existsByCorreo(correo)) {
            throw new ConflictoExcepcion("Ya existe un usuario con el correo '%s'".formatted(correo));
        }
        if (usuarioRepositorio.existsByIdentificacion(identificacion)) {
            throw new ConflictoExcepcion("Ya existe un usuario con la identificación '%s'".formatted(identificacion));
        }
        Rol rol = obtenerRol(dto.rolId());
        Usuario usuario = usuarioMapeador.aEntidad(dto, rol, codificadorContrasena.encode(dto.contrasena()));
        return usuarioMapeador.aRespuesta(usuarioRepositorio.save(usuario));
    }

    @Override
    @Transactional
    public UsuarioRespuestaDto actualizar(Integer id, UsuarioActualizarDto dto) {
        Usuario usuario = obtenerUsuario(id);
        String correo = usuarioMapeador.normalizarCorreo(dto.correo());
        String identificacion = dto.identificacion().trim();
        if (usuarioRepositorio.existsByCorreoAndIdNot(correo, id)) {
            throw new ConflictoExcepcion("Ya existe un usuario con el correo '%s'".formatted(correo));
        }
        if (usuarioRepositorio.existsByIdentificacionAndIdNot(identificacion, id)) {
            throw new ConflictoExcepcion("Ya existe un usuario con la identificación '%s'".formatted(identificacion));
        }
        usuarioMapeador.actualizarEntidad(usuario, dto, obtenerRol(dto.rolId()));
        return usuarioMapeador.aRespuesta(usuario);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        obtenerUsuario(id).setActivo(false);
    }

    private Usuario obtenerUsuario(Integer id) {
        return usuarioRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion(RECURSO, id));
    }

    private Rol obtenerRol(Integer rolId) {
        return rolRepositorio.findById(rolId)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion(RECURSO_ROL, rolId));
    }
}
