package autobaterias.demo.controladores;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import autobaterias.demo.entidades.Bateria;
import autobaterias.demo.entidades.Marca;
import autobaterias.demo.entidades.Rol;
import autobaterias.demo.entidades.Usuario;
import autobaterias.demo.excepciones.ManejadorGlobalExcepciones;
import autobaterias.demo.mapeadores.BateriaMapeador;
import autobaterias.demo.mapeadores.RolMapeador;
import autobaterias.demo.mapeadores.UsuarioMapeador;
import autobaterias.demo.repositorios.IBateriaRepositorio;
import autobaterias.demo.repositorios.IMarcaRepositorio;
import autobaterias.demo.repositorios.IRolRepositorio;
import autobaterias.demo.repositorios.IUsuarioRepositorio;
import autobaterias.demo.servicios.Impl.BateriaServicioImpl;
import autobaterias.demo.servicios.Impl.RolServicioImpl;
import autobaterias.demo.servicios.Impl.UsuarioServicioImpl;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(MockitoExtension.class)
class ContratosPostMockMvcTest {

    @Mock
    private IRolRepositorio rolRepositorio;

    @Mock
    private IUsuarioRepositorio usuarioRepositorio;

    @Mock
    private IBateriaRepositorio bateriaRepositorio;

    @Mock
    private IMarcaRepositorio marcaRepositorio;

    @Mock
    private PasswordEncoder codificadorContrasena;

    private LocalValidatorFactoryBean validador;
    private MockMvc mockMvc;

    private final Rol administradora = Rol.builder().id(1).nombre("ADMINISTRADORA").build();
    private final Marca mac = Marca.builder().id(1).nombre("MAC").build();

    @BeforeEach
    void configurar() {
        validador = new LocalValidatorFactoryBean();
        validador.afterPropertiesSet();

        RolControlador rolControlador = new RolControlador(
                new RolServicioImpl(rolRepositorio, usuarioRepositorio, new RolMapeador()));
        UsuarioControlador usuarioControlador = new UsuarioControlador(
                new UsuarioServicioImpl(usuarioRepositorio, rolRepositorio, new UsuarioMapeador(), codificadorContrasena));
        BateriaControlador bateriaControlador = new BateriaControlador(
                new BateriaServicioImpl(bateriaRepositorio, marcaRepositorio, new BateriaMapeador()));

        mockMvc = MockMvcBuilders.standaloneSetup(rolControlador, usuarioControlador, bateriaControlador)
                .setControllerAdvice(new ManejadorGlobalExcepciones())
                .setValidator(validador)
                .setMessageConverters(new JacksonJsonHttpMessageConverter())
                .build();
    }

    @AfterEach
    void cerrar() {
        validador.close();
    }

    // --- POST /roles ---

    @Test
    void postRolExitosoNormalizaNombreYResponde201() throws Exception {
        when(rolRepositorio.existsByNombreIgnoreCase("VENDEDOR")).thenReturn(false);
        when(rolRepositorio.save(any(Rol.class))).thenAnswer(inv -> {
            Rol rol = inv.getArgument(0);
            rol.setId(2);
            return rol;
        });

        mockMvc.perform(post("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "  vendedor "
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.nombre").value("VENDEDOR"));
    }

    @Test
    void postRolDuplicadoResponde409YNoGuarda() throws Exception {
        when(rolRepositorio.existsByNombreIgnoreCase("ADMINISTRADORA")).thenReturn(true);

        mockMvc.perform(post("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "administradora"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.estado").value(409))
                .andExpect(jsonPath("$.mensaje", containsString("ADMINISTRADORA")));

        verify(rolRepositorio, never()).save(any());
    }

    @Test
    void postRolVacioONuloResponde400YNoGuarda() throws Exception {
        mockMvc.perform(post("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "   "
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.mensaje", containsString("nombre")));

        mockMvc.perform(post("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.mensaje", containsString("nombre")));

        verify(rolRepositorio, never()).save(any());
    }

    @Test
    void postRolSuperaLongitudMaximaResponde400() throws Exception {
        String nombreLargo = "A".repeat(51);

        mockMvc.perform(post("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"" + nombreLargo + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.mensaje", containsString("nombre")));

        verify(rolRepositorio, never()).save(any());
    }

    @Test
    void postRolJsonMalFormadoResponde400() throws Exception {
        mockMvc.perform(post("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.mensaje").value("Solicitud mal formada"));

        verify(rolRepositorio, never()).save(any());
    }

    // --- POST /usuarios ---

    @Test
    void postUsuarioExitosoResponde201SinExponerContrasena() throws Exception {
        when(usuarioRepositorio.existsByCorreo("ana@autobaterias.com")).thenReturn(false);
        when(usuarioRepositorio.existsByIdentificacion("1053000111")).thenReturn(false);
        when(rolRepositorio.findById(1)).thenReturn(Optional.of(administradora));
        when(codificadorContrasena.encode("Secreta123")).thenReturn("$2a$10$hash");
        when(usuarioRepositorio.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario usuario = inv.getArgument(0);
            usuario.setId(10);
            return usuario;
        });

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "identificacion": " 1053000111 ",
                                  "nombre": " Ana ",
                                  "apellido": " Gomez ",
                                  "telefono": " 3001234567 ",
                                  "correo": "Ana@AutoBaterias.com",
                                  "rolId": 1,
                                  "contrasena": "Secreta123"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.identificacion").value("1053000111"))
                .andExpect(jsonPath("$.correo").value("ana@autobaterias.com"))
                .andExpect(jsonPath("$.nombreRol").value("ADMINISTRADORA"))
                .andExpect(jsonPath("$.activo").value(true))
                .andExpect(jsonPath("$.contrasena").doesNotExist());
    }

    @Test
    void postUsuarioCorreoDuplicadoResponde409YNoGuarda() throws Exception {
        when(usuarioRepositorio.existsByCorreo("ana@autobaterias.com")).thenReturn(true);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "identificacion": "999",
                                  "nombre": "Ana",
                                  "apellido": "Gomez",
                                  "telefono": "3001234567",
                                  "correo": "ANA@AutoBaterias.com",
                                  "rolId": 1,
                                  "contrasena": "Secreta123"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.estado").value(409))
                .andExpect(jsonPath("$.mensaje", containsString("correo")));

        verify(usuarioRepositorio, never()).save(any());
        verify(codificadorContrasena, never()).encode(any());
    }

    @Test
    void postUsuarioIdentificacionDuplicadaResponde409YNoGuarda() throws Exception {
        when(usuarioRepositorio.existsByCorreo("otro@autobaterias.com")).thenReturn(false);
        when(usuarioRepositorio.existsByIdentificacion("1053000111")).thenReturn(true);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "identificacion": " 1053000111 ",
                                  "nombre": "Ana",
                                  "apellido": "Gomez",
                                  "telefono": "3001234567",
                                  "correo": "otro@autobaterias.com",
                                  "rolId": 1,
                                  "contrasena": "Secreta123"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.estado").value(409))
                .andExpect(jsonPath("$.mensaje", containsString("identificación")));

        verify(usuarioRepositorio, never()).save(any());
    }

    @Test
    void postUsuarioRolInexistenteResponde404YNoGuarda() throws Exception {
        when(usuarioRepositorio.existsByCorreo("b@autobaterias.com")).thenReturn(false);
        when(usuarioRepositorio.existsByIdentificacion("222")).thenReturn(false);
        when(rolRepositorio.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "identificacion": "222",
                                  "nombre": "Ana",
                                  "apellido": "Gomez",
                                  "telefono": "3001234567",
                                  "correo": "b@autobaterias.com",
                                  "rolId": 99,
                                  "contrasena": "Secreta123"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.mensaje").value("Rol con id 99 no existe"));

        verify(usuarioRepositorio, never()).save(any());
        verify(codificadorContrasena, never()).encode(any());
    }

    @Test
    void postUsuarioDatosInvalidosYFaltantesResponde400YNoGuarda() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "identificacion": "",
                                  "nombre": "",
                                  "apellido": "Gomez",
                                  "telefono": "3001234567",
                                  "correo": "no-es-correo",
                                  "rolId": 0,
                                  "contrasena": "123"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.mensaje", containsString("correo")))
                .andExpect(jsonPath("$.mensaje", containsString("contrasena")))
                .andExpect(jsonPath("$.mensaje", containsString("identificacion")))
                .andExpect(jsonPath("$.mensaje", containsString("rolId")));

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "identificacion": "333",
                                  "nombre": "Ana",
                                  "apellido": "Gomez",
                                  "telefono": "3001234567",
                                  "correo": "c@autobaterias.com"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.mensaje", containsString("rolId")))
                .andExpect(jsonPath("$.mensaje", containsString("contrasena")));

        verify(usuarioRepositorio, never()).save(any());
    }

    @Test
    void postUsuarioJsonMalFormadoResponde400() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.mensaje").value("Solicitud mal formada"));

        verify(usuarioRepositorio, never()).save(any());
    }

    // --- POST /baterias ---

    @Test
    void postBateriaExitosoNormalizaCodigoYResponde201() throws Exception {
        when(bateriaRepositorio.existsByCodigo("MAC-42")).thenReturn(false);
        when(marcaRepositorio.findById(1)).thenReturn(Optional.of(mac));
        when(bateriaRepositorio.save(any(Bateria.class))).thenAnswer(inv -> {
            Bateria bateria = inv.getArgument(0);
            bateria.setId(1);
            return bateria;
        });

        mockMvc.perform(post("/baterias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": " mac-42 ",
                                  "modelo": " Silver 42 ",
                                  "marcaId": 1,
                                  "precio": 450000.00,
                                  "mesesGarantia": 12,
                                  "existencias": 8
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.codigo").value("MAC-42"))
                .andExpect(jsonPath("$.modelo").value("Silver 42"))
                .andExpect(jsonPath("$.marcaId").value(1))
                .andExpect(jsonPath("$.nombreMarca").value("MAC"))
                .andExpect(jsonPath("$.activo").value(true));
    }

    @Test
    void postBateriaCodigoDuplicadoResponde409YNoGuarda() throws Exception {
        when(bateriaRepositorio.existsByCodigo("MAC-42")).thenReturn(true);

        mockMvc.perform(post("/baterias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": " mac-42 ",
                                  "modelo": "Otra",
                                  "marcaId": 1,
                                  "precio": 450000.00,
                                  "mesesGarantia": 12,
                                  "existencias": 8
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.estado").value(409))
                .andExpect(jsonPath("$.mensaje", containsString("MAC-42")));

        verify(bateriaRepositorio, never()).save(any());
    }

    @Test
    void postBateriaMarcaInexistenteResponde404YNoGuarda() throws Exception {
        when(bateriaRepositorio.existsByCodigo("X-1")).thenReturn(false);
        when(marcaRepositorio.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(post("/baterias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "X-1",
                                  "modelo": "Silver 42",
                                  "marcaId": 99,
                                  "precio": 450000.00,
                                  "mesesGarantia": 12,
                                  "existencias": 8
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.mensaje").value("Marca con id 99 no existe"));

        verify(bateriaRepositorio, never()).save(any());
    }

    @Test
    void postBateriaDatosInvalidosYDecimalesResponde400YNoGuarda() throws Exception {
        mockMvc.perform(post("/baterias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "",
                                  "modelo": "",
                                  "marcaId": 0,
                                  "precio": -5,
                                  "mesesGarantia": -1,
                                  "existencias": -2
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.mensaje", containsString("codigo")))
                .andExpect(jsonPath("$.mensaje", containsString("precio")));

        mockMvc.perform(post("/baterias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "X-2",
                                  "modelo": "Silver 42",
                                  "marcaId": 1,
                                  "precio": 10.123,
                                  "mesesGarantia": 12,
                                  "existencias": 8
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.mensaje", containsString("precio")));

        verify(bateriaRepositorio, never()).save(any());
    }
}
