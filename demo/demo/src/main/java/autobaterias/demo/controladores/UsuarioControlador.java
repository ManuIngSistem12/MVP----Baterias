package autobaterias.demo.controladores;

import autobaterias.demo.dtos.usuario.UsuarioActualizarDto;
import autobaterias.demo.dtos.usuario.UsuarioCrearDto;
import autobaterias.demo.dtos.usuario.UsuarioRespuestaDto;
import autobaterias.demo.servicios.IUsuarioServicio;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioControlador {

    private final IUsuarioServicio usuarioServicio;

    @GetMapping
    public ResponseEntity<List<UsuarioRespuestaDto>> listarTodos() {
        return ResponseEntity.ok(usuarioServicio.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioRespuestaDto> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioServicio.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioRespuestaDto> guardar(@Valid @RequestBody UsuarioCrearDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioServicio.guardar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioRespuestaDto> actualizar(@PathVariable Integer id, @Valid @RequestBody UsuarioActualizarDto dto) {
        return ResponseEntity.ok(usuarioServicio.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        usuarioServicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
