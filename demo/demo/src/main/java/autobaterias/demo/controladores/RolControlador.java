package autobaterias.demo.controladores;

import autobaterias.demo.dtos.rol.RolRespuestaDto;
import autobaterias.demo.dtos.rol.RolSolicitudDto;
import autobaterias.demo.servicios.IRolServicio;
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
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RolControlador {

    private final IRolServicio rolServicio;

    @GetMapping
    public ResponseEntity<List<RolRespuestaDto>> listarTodos() {
        return ResponseEntity.ok(rolServicio.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolRespuestaDto> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(rolServicio.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<RolRespuestaDto> guardar(@Valid @RequestBody RolSolicitudDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rolServicio.guardar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RolRespuestaDto> actualizar(@PathVariable Integer id, @Valid @RequestBody RolSolicitudDto dto) {
        return ResponseEntity.ok(rolServicio.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        rolServicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
