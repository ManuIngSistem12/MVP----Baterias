package autobaterias.demo.servicios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import autobaterias.demo.servicios.Impl.UsuarioServicioImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UsuarioServicioImplTest {

    @Mock
    private IUsuarioRepositorio usuarioRepositorio;

    @Mock
    private IRolRepositorio rolRepositorio;

    @Mock
    private PasswordEncoder codificadorContrasena;

    private UsuarioServicioImpl usuarioServicio;

    private final Rol administradora = Rol.builder().id(1).nombre("ADMINISTRADORA").build();

    @BeforeEach
    void configurar() {
        usuarioServicio = new UsuarioServicioImpl(usuarioRepositorio, rolRepositorio, new UsuarioMapeador(), codificadorContrasena);
    }

    private UsuarioCrearDto crearDto(String correo, Integer rolId) {
        return new UsuarioCrearDto("1053000111", "Ana", "Gomez", "3001234567", correo, rolId, "Secreta123");
    }

    private Usuario usuarioExistente() {
        return Usuario.builder()
                .id(5).identificacion("1053000111").nombre("Ana").apellido("Gomez")
                .telefono("3001234567").correo("ana@autobaterias.com")
                .rol(administradora).contrasena("$2a$10$hash").build();
    }

    @Test
    void guardarCreaUsuarioConContrasenaCifradaYCorreoNormalizado() {
        when(usuarioRepositorio.existsByCorreo("ana@autobaterias.com")).thenReturn(false);
        when(usuarioRepositorio.existsByIdentificacion("1053000111")).thenReturn(false);
        when(rolRepositorio.findById(1)).thenReturn(Optional.of(administradora));
        when(codificadorContrasena.encode("Secreta123")).thenReturn("$2a$10$hash");
        when(usuarioRepositorio.save(any(Usuario.class))).thenAnswer(invocacion -> {
            Usuario usuario = invocacion.getArgument(0);
            usuario.setId(10);
            return usuario;
        });

        UsuarioRespuestaDto respuesta = usuarioServicio.guardar(crearDto("  Ana@AutoBaterias.com ", 1));

        ArgumentCaptor<Usuario> guardado = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepositorio).save(guardado.capture());
        assertThat(guardado.getValue().getContrasena()).isEqualTo("$2a$10$hash").isNotEqualTo("Secreta123");
        assertThat(guardado.getValue().getActivo()).isTrue();
        assertThat(respuesta.id()).isEqualTo(10);
        assertThat(respuesta.correo()).isEqualTo("ana@autobaterias.com");
        assertThat(respuesta.nombreRol()).isEqualTo("ADMINISTRADORA");
        assertThat(respuesta.activo()).isTrue();
    }

    @Test
    void guardarConCorreoDuplicadoLanzaConflicto() {
        when(usuarioRepositorio.existsByCorreo("ana@autobaterias.com")).thenReturn(true);

        assertThatThrownBy(() -> usuarioServicio.guardar(crearDto("ana@autobaterias.com", 1)))
                .isInstanceOf(ConflictoExcepcion.class)
                .hasMessageContaining("correo")
                .extracting("estado").isEqualTo(HttpStatus.CONFLICT);
        verify(usuarioRepositorio, never()).save(any());
    }

    @Test
    void guardarConIdentificacionDuplicadaLanzaConflicto() {
        when(usuarioRepositorio.existsByCorreo("ana@autobaterias.com")).thenReturn(false);
        when(usuarioRepositorio.existsByIdentificacion("1053000111")).thenReturn(true);

        assertThatThrownBy(() -> usuarioServicio.guardar(crearDto("ana@autobaterias.com", 1)))
                .isInstanceOf(ConflictoExcepcion.class)
                .hasMessageContaining("identificación");
        verify(usuarioRepositorio, never()).save(any());
    }

    @Test
    void guardarConRolInexistenteLanzaNoEncontrado() {
        when(usuarioRepositorio.existsByCorreo("ana@autobaterias.com")).thenReturn(false);
        when(usuarioRepositorio.existsByIdentificacion("1053000111")).thenReturn(false);
        when(rolRepositorio.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioServicio.guardar(crearDto("ana@autobaterias.com", 99)))
                .isInstanceOf(RecursoNoEncontradoExcepcion.class)
                .hasMessage("Rol con id 99 no encontrado")
                .extracting("estado").isEqualTo(HttpStatus.NOT_FOUND);
        verify(usuarioRepositorio, never()).save(any());
        verify(codificadorContrasena, never()).encode(any());
    }

    @Test
    void eliminarDesactivaElUsuarioSinBorrarlo() {
        Usuario usuario = usuarioExistente();
        when(usuarioRepositorio.findById(5)).thenReturn(Optional.of(usuario));

        usuarioServicio.eliminar(5);

        assertThat(usuario.getActivo()).isFalse();
        verify(usuarioRepositorio, never()).delete(any());
        verify(usuarioRepositorio, never()).deleteById(any());
    }

    @Test
    void eliminarUsuarioInexistenteLanzaNoEncontrado() {
        when(usuarioRepositorio.findById(404)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioServicio.eliminar(404))
                .isInstanceOf(RecursoNoEncontradoExcepcion.class)
                .hasMessage("Usuario con id 404 no encontrado");
    }

    @Test
    void actualizarConCorreoDeOtroUsuarioLanzaConflicto() {
        when(usuarioRepositorio.findById(5)).thenReturn(Optional.of(usuarioExistente()));
        when(usuarioRepositorio.existsByCorreoAndIdNot("otro@autobaterias.com", 5)).thenReturn(true);
        UsuarioActualizarDto dto = new UsuarioActualizarDto("1053000111", "Ana", "Gomez", "3001234567", "otro@autobaterias.com", 1);

        assertThatThrownBy(() -> usuarioServicio.actualizar(5, dto))
                .isInstanceOf(ConflictoExcepcion.class);
    }

    @Test
    void actualizarModificaDatosSinTocarLaContrasena() {
        Usuario usuario = usuarioExistente();
        when(usuarioRepositorio.findById(5)).thenReturn(Optional.of(usuario));
        when(rolRepositorio.findById(1)).thenReturn(Optional.of(administradora));
        UsuarioActualizarDto dto = new UsuarioActualizarDto("1053000111", "Ana Maria", "Gomez", "3109876543", "ana@autobaterias.com", 1);

        UsuarioRespuestaDto respuesta = usuarioServicio.actualizar(5, dto);

        assertThat(respuesta.nombre()).isEqualTo("Ana Maria");
        assertThat(respuesta.telefono()).isEqualTo("3109876543");
        assertThat(usuario.getContrasena()).isEqualTo("$2a$10$hash");
    }
}
