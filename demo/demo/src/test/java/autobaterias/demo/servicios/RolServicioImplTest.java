package autobaterias.demo.servicios;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import autobaterias.demo.entidades.Rol;
import autobaterias.demo.excepciones.ConflictoExcepcion;
import autobaterias.demo.mapeadores.RolMapeador;
import autobaterias.demo.repositorios.IRolRepositorio;
import autobaterias.demo.repositorios.IUsuarioRepositorio;
import autobaterias.demo.servicios.Impl.RolServicioImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RolServicioImplTest {

    @Mock
    private IRolRepositorio rolRepositorio;

    @Mock
    private IUsuarioRepositorio usuarioRepositorio;

    private RolServicioImpl rolServicio;

    @BeforeEach
    void configurar() {
        rolServicio = new RolServicioImpl(rolRepositorio, usuarioRepositorio, new RolMapeador());
    }

    @Test
    void eliminarRolAsignadoAUsuariosLanzaConflicto() {
        when(rolRepositorio.findById(1)).thenReturn(Optional.of(Rol.builder().id(1).nombre("ADMINISTRADORA").build()));
        when(usuarioRepositorio.existsByRolId(1)).thenReturn(true);

        assertThatThrownBy(() -> rolServicio.eliminar(1))
                .isInstanceOf(ConflictoExcepcion.class)
                .hasMessage("El rol 'ADMINISTRADORA' está asignado a usuarios y no se puede eliminar");
        verify(rolRepositorio, never()).delete(any());
    }

    @Test
    void eliminarRolSinUsuariosLoBorra() {
        Rol rol = Rol.builder().id(2).nombre("VENDEDOR").build();
        when(rolRepositorio.findById(2)).thenReturn(Optional.of(rol));
        when(usuarioRepositorio.existsByRolId(2)).thenReturn(false);

        rolServicio.eliminar(2);

        verify(rolRepositorio).delete(rol);
    }
}
