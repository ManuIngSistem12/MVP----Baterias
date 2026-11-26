package autobaterias.demo.controladores;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import autobaterias.demo.entidades.Bateria;
import autobaterias.demo.entidades.Marca;
import autobaterias.demo.excepciones.ManejadorGlobalExcepciones;
import autobaterias.demo.mapeadores.BateriaMapeador;
import autobaterias.demo.repositorios.IBateriaRepositorio;
import autobaterias.demo.repositorios.IMarcaRepositorio;
import autobaterias.demo.servicios.Impl.BateriaServicioImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(MockitoExtension.class)
class BateriaControladorMockMvcTest {

    @Mock
    private IBateriaRepositorio bateriaRepositorio;

    @Mock
    private IMarcaRepositorio marcaRepositorio;

    private LocalValidatorFactoryBean validador;
    private MockMvc mockMvc;

    private final Marca mac = Marca.builder().id(1).nombre("MAC").build();
    private final Marca willard = Marca.builder().id(2).nombre("WILLARD").build();

    @BeforeEach
    void configurar() {
        validador = new LocalValidatorFactoryBean();
        validador.afterPropertiesSet();

        BateriaControlador bateriaControlador = new BateriaControlador(
                new BateriaServicioImpl(bateriaRepositorio, marcaRepositorio, new BateriaMapeador()));

        mockMvc = MockMvcBuilders.standaloneSetup(bateriaControlador)
                .setControllerAdvice(new ManejadorGlobalExcepciones())
                .setValidator(validador)
                .setMessageConverters(new JacksonJsonHttpMessageConverter())
                .build();
    }

    @AfterEach
    void cerrar() {
        validador.close();
    }

    private Bateria bateriaMac() {
        return Bateria.builder()
                .id(1)
                .codigo("MAC-42")
                .modelo("Silver 42")
                .marca(mac)
                .precio(new BigDecimal("450000.00"))
                .mesesGarantia(12)
                .existencias(8)
                .activo(true)
                .build();
    }

    @Test
    void listarBateriasResponde200ConListado() throws Exception {
        Bateria b1 = bateriaMac();
        Bateria b2 = Bateria.builder()
                .id(2).codigo("WIL-60").modelo("Power 60").marca(willard)
                .precio(new BigDecimal("520000.00")).mesesGarantia(18).existencias(5).activo(true).build();
        when(bateriaRepositorio.findAll()).thenReturn(List.of(b1, b2));

        mockMvc.perform(get("/baterias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].codigo").value("MAC-42"))
                .andExpect(jsonPath("$[0].nombreMarca").value("MAC"))
                .andExpect(jsonPath("$[1].codigo").value("WIL-60"))
                .andExpect(jsonPath("$[1].nombreMarca").value("WILLARD"));
    }

    @Test
    void buscarPorIdExistenteResponde200() throws Exception {
        when(bateriaRepositorio.findById(1)).thenReturn(Optional.of(bateriaMac()));

        mockMvc.perform(get("/baterias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.codigo").value("MAC-42"))
                .andExpect(jsonPath("$.mesesGarantia").value(12))
                .andExpect(jsonPath("$.activo").value(true));
    }

    @Test
    void buscarPorIdInexistenteResponde404() throws Exception {
        when(bateriaRepositorio.findById(9999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/baterias/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.mensaje").value("Batería con id 9999 no existe"));
    }

    @Test
    void buscarPorIdNoNumericoResponde400() throws Exception {
        mockMvc.perform(get("/baterias/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.mensaje").value("Solicitud mal formada"));
    }

    @Test
    void crearBateriaConValoresLimiteEnCeroResponde201() throws Exception {
        when(bateriaRepositorio.existsByCodigo("LIM-0")).thenReturn(false);
        when(marcaRepositorio.findById(1)).thenReturn(Optional.of(mac));
        when(bateriaRepositorio.save(any(Bateria.class))).thenAnswer(inv -> {
            Bateria b = inv.getArgument(0);
            b.setId(3);
            return b;
        });

        mockMvc.perform(post("/baterias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": " lim-0 ",
                                  "modelo": " Base 0 ",
                                  "marcaId": 1,
                                  "precio": 0.00,
                                  "mesesGarantia": 0,
                                  "existencias": 0
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.codigo").value("LIM-0"))
                .andExpect(jsonPath("$.modelo").value("Base 0"))
                .andExpect(jsonPath("$.precio").value(0.0))
                .andExpect(jsonPath("$.mesesGarantia").value(0))
                .andExpect(jsonPath("$.existencias").value(0))
                .andExpect(jsonPath("$.activo").value(true));
    }

    @Test
    void crearBateriaSinCamposObligatoriosResponde400YNoGuarda() throws Exception {
        mockMvc.perform(post("/baterias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.mensaje", containsString("codigo")))
                .andExpect(jsonPath("$.mensaje", containsString("modelo")))
                .andExpect(jsonPath("$.mensaje", containsString("marcaId")))
                .andExpect(jsonPath("$.mensaje", containsString("precio")))
                .andExpect(jsonPath("$.mensaje", containsString("mesesGarantia")))
                .andExpect(jsonPath("$.mensaje", containsString("existencias")));

        verify(bateriaRepositorio, never()).save(any());
    }

    @Test
    void crearBateriaCodigoDuplicadoResponde409YNoGuarda() throws Exception {
        when(bateriaRepositorio.existsByCodigo("MAC-42")).thenReturn(true);

        mockMvc.perform(post("/baterias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "MAC-42",
                                  "modelo": "Duplicada",
                                  "marcaId": 1,
                                  "precio": 450000.00,
                                  "mesesGarantia": 12,
                                  "existencias": 8
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.estado").value(409))
                .andExpect(jsonPath("$.mensaje").value("Ya existe una batería con el código 'MAC-42'"));

        verify(bateriaRepositorio, never()).save(any());
    }

    @Test
    void actualizarBateriaExitosaResponde200() throws Exception {
        Bateria existente = bateriaMac();
        when(bateriaRepositorio.findById(1)).thenReturn(Optional.of(existente));
        when(bateriaRepositorio.existsByCodigoAndIdNot("MAC-42", 1)).thenReturn(false);
        when(marcaRepositorio.findById(2)).thenReturn(Optional.of(willard));

        mockMvc.perform(put("/baterias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": " mac-42 ",
                                  "modelo": "Silver 42 Plus",
                                  "marcaId": 2,
                                  "precio": 480000.50,
                                  "mesesGarantia": 18,
                                  "existencias": 3
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.codigo").value("MAC-42"))
                .andExpect(jsonPath("$.modelo").value("Silver 42 Plus"))
                .andExpect(jsonPath("$.marcaId").value(2))
                .andExpect(jsonPath("$.nombreMarca").value("WILLARD"))
                .andExpect(jsonPath("$.precio").value(480000.5))
                .andExpect(jsonPath("$.mesesGarantia").value(18))
                .andExpect(jsonPath("$.existencias").value(3));
    }

    @Test
    void actualizarBateriaConCodigoDeOtraResponde409SinModificarDatos() throws Exception {
        Bateria existente = bateriaMac();
        when(bateriaRepositorio.findById(1)).thenReturn(Optional.of(existente));
        when(bateriaRepositorio.existsByCodigoAndIdNot("WIL-60", 1)).thenReturn(true);

        mockMvc.perform(put("/baterias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "WIL-60",
                                  "modelo": "Modelo Nuevo",
                                  "marcaId": 2,
                                  "precio": 999999.00,
                                  "mesesGarantia": 24,
                                  "existencias": 99
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.estado").value(409))
                .andExpect(jsonPath("$.mensaje").value("Ya existe una batería con el código 'WIL-60'"));

        assertThat(existente.getCodigo()).isEqualTo("MAC-42");
        assertThat(existente.getModelo()).isEqualTo("Silver 42");
        assertThat(existente.getMarca()).isEqualTo(mac);
        assertThat(existente.getPrecio()).isEqualByComparingTo("450000.00");
        assertThat(existente.getMesesGarantia()).isEqualTo(12);
        assertThat(existente.getExistencias()).isEqualTo(8);
    }

    @Test
    void actualizarBateriaConDatosInvalidosResponde400SinModificarDatos() throws Exception {
        mockMvc.perform(put("/baterias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "MAC-42",
                                  "modelo": "Silver 42",
                                  "marcaId": 1,
                                  "precio": -10.00,
                                  "mesesGarantia": 12,
                                  "existencias": 8
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.mensaje", containsString("precio")));

        verify(bateriaRepositorio, never()).findById(any());
    }

    @Test
    void actualizarBateriaInexistenteOMarcaInexistenteResponde404() throws Exception {
        when(bateriaRepositorio.findById(9999)).thenReturn(Optional.empty());

        mockMvc.perform(put("/baterias/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "MAC-42",
                                  "modelo": "Silver 42",
                                  "marcaId": 1,
                                  "precio": 450000.00,
                                  "mesesGarantia": 12,
                                  "existencias": 8
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.mensaje").value("Batería con id 9999 no existe"));

        Bateria existente = bateriaMac();
        when(bateriaRepositorio.findById(1)).thenReturn(Optional.of(existente));
        when(bateriaRepositorio.existsByCodigoAndIdNot("MAC-42", 1)).thenReturn(false);
        when(marcaRepositorio.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(put("/baterias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "MAC-42",
                                  "modelo": "Nuevo",
                                  "marcaId": 99,
                                  "precio": 500000.00,
                                  "mesesGarantia": 24,
                                  "existencias": 10
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.mensaje").value("Marca con id 99 no existe"));

        assertThat(existente.getModelo()).isEqualTo("Silver 42");
        assertThat(existente.getMarca()).isEqualTo(mac);
    }

    @Test
    void eliminarBateriaRealizaBorradoLogicoYResponde204() throws Exception {
        Bateria existente = bateriaMac();
        when(bateriaRepositorio.findById(1)).thenReturn(Optional.of(existente));

        mockMvc.perform(delete("/baterias/1"))
                .andExpect(status().isNoContent());

        assertThat(existente.getActivo()).isFalse();
        verify(bateriaRepositorio, never()).delete(any());
        verify(bateriaRepositorio, never()).deleteById(any());
    }

    @Test
    void eliminarBateriaInexistenteResponde404() throws Exception {
        when(bateriaRepositorio.findById(9999)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/baterias/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.mensaje").value("Batería con id 9999 no existe"));
    }
}
