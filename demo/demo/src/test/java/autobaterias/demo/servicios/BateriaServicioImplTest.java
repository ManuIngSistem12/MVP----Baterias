package autobaterias.demo.servicios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import autobaterias.demo.dtos.bateria.BateriaRespuestaDto;
import autobaterias.demo.dtos.bateria.BateriaSolicitudDto;
import autobaterias.demo.entidades.Bateria;
import autobaterias.demo.entidades.Marca;
import autobaterias.demo.excepciones.ConflictoExcepcion;
import autobaterias.demo.excepciones.RecursoNoEncontradoExcepcion;
import autobaterias.demo.mapeadores.BateriaMapeador;
import autobaterias.demo.repositorios.IBateriaRepositorio;
import autobaterias.demo.repositorios.IMarcaRepositorio;
import autobaterias.demo.servicios.Impl.BateriaServicioImpl;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class BateriaServicioImplTest {

    @Mock
    private IBateriaRepositorio bateriaRepositorio;

    @Mock
    private IMarcaRepositorio marcaRepositorio;

    private BateriaServicioImpl bateriaServicio;

    private final Marca mac = Marca.builder().id(1).nombre("MAC").build();

    @BeforeEach
    void configurar() {
        bateriaServicio = new BateriaServicioImpl(bateriaRepositorio, marcaRepositorio, new BateriaMapeador());
    }

    private BateriaSolicitudDto solicitud(String codigo, Integer marcaId) {
        return new BateriaSolicitudDto(codigo, "Silver 42", marcaId, new BigDecimal("450000.00"), 12, 8);
    }

    private Bateria bateriaExistente() {
        return Bateria.builder()
                .id(7).codigo("MAC-42").modelo("Silver 42").marca(mac)
                .precio(new BigDecimal("450000.00")).mesesGarantia(12).existencias(8).build();
    }

    @Test
    void guardarCreaBateriaConCodigoNormalizadoYActiva() {
        when(bateriaRepositorio.existsByCodigo("MAC-42")).thenReturn(false);
        when(marcaRepositorio.findById(1)).thenReturn(Optional.of(mac));
        when(bateriaRepositorio.save(any(Bateria.class))).thenAnswer(invocacion -> {
            Bateria bateria = invocacion.getArgument(0);
            bateria.setId(7);
            return bateria;
        });

        BateriaRespuestaDto respuesta = bateriaServicio.guardar(solicitud("  mac-42 ", 1));

        ArgumentCaptor<Bateria> guardada = ArgumentCaptor.forClass(Bateria.class);
        verify(bateriaRepositorio).save(guardada.capture());
        assertThat(guardada.getValue().getActivo()).isTrue();
        assertThat(respuesta.id()).isEqualTo(7);
        assertThat(respuesta.codigo()).isEqualTo("MAC-42");
        assertThat(respuesta.nombreMarca()).isEqualTo("MAC");
        assertThat(respuesta.precio()).isEqualByComparingTo("450000");
    }

    @Test
    void guardarConCodigoDuplicadoLanzaConflicto() {
        when(bateriaRepositorio.existsByCodigo("MAC-42")).thenReturn(true);

        assertThatThrownBy(() -> bateriaServicio.guardar(solicitud("MAC-42", 1)))
                .isInstanceOf(ConflictoExcepcion.class)
                .hasMessage("Ya existe una batería con el código 'MAC-42'")
                .extracting("estado").isEqualTo(HttpStatus.CONFLICT);
        verify(bateriaRepositorio, never()).save(any());
    }

    @Test
    void guardarConMarcaInexistenteLanzaNoEncontrado() {
        when(bateriaRepositorio.existsByCodigo("MAC-42")).thenReturn(false);
        when(marcaRepositorio.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bateriaServicio.guardar(solicitud("MAC-42", 99)))
                .isInstanceOf(RecursoNoEncontradoExcepcion.class)
                .hasMessage("Marca con id 99 no existe")
                .extracting("estado").isEqualTo(HttpStatus.NOT_FOUND);
        verify(bateriaRepositorio, never()).save(any());
    }

    @Test
    void actualizarConCodigoDeOtraBateriaLanzaConflicto() {
        when(bateriaRepositorio.findById(7)).thenReturn(Optional.of(bateriaExistente()));
        when(bateriaRepositorio.existsByCodigoAndIdNot("WIL-60", 7)).thenReturn(true);

        assertThatThrownBy(() -> bateriaServicio.actualizar(7, solicitud("WIL-60", 1)))
                .isInstanceOf(ConflictoExcepcion.class);
    }

    @Test
    void actualizarModificaPrecioYExistencias() {
        Bateria bateria = bateriaExistente();
        when(bateriaRepositorio.findById(7)).thenReturn(Optional.of(bateria));
        when(marcaRepositorio.findById(1)).thenReturn(Optional.of(mac));
        BateriaSolicitudDto dto = new BateriaSolicitudDto("MAC-42", "Silver 42", 1, new BigDecimal("480000.50"), 18, 3);

        BateriaRespuestaDto respuesta = bateriaServicio.actualizar(7, dto);

        assertThat(respuesta.precio()).isEqualByComparingTo("480000.50");
        assertThat(respuesta.mesesGarantia()).isEqualTo(18);
        assertThat(respuesta.existencias()).isEqualTo(3);
    }

    @Test
    void eliminarDesactivaLaBateriaSinBorrarla() {
        Bateria bateria = bateriaExistente();
        when(bateriaRepositorio.findById(7)).thenReturn(Optional.of(bateria));

        bateriaServicio.eliminar(7);

        assertThat(bateria.getActivo()).isFalse();
        verify(bateriaRepositorio, never()).delete(any());
        verify(bateriaRepositorio, never()).deleteById(any());
    }

    @Test
    void buscarBateriaInexistenteLanzaNoEncontrado() {
        when(bateriaRepositorio.findById(404)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bateriaServicio.buscarPorId(404))
                .isInstanceOf(RecursoNoEncontradoExcepcion.class)
                .hasMessage("Batería con id 404 no existe");
    }
}
