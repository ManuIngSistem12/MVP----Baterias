package autobaterias.demo.controladores;

import autobaterias.demo.dtos.bateria.BateriaRespuestaDto;
import autobaterias.demo.dtos.bateria.BateriaSolicitudDto;
import autobaterias.demo.servicios.IBateriaServicio;
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
@RequestMapping("/baterias")
@RequiredArgsConstructor
public class BateriaControlador {

    private final IBateriaServicio bateriaServicio;

    @GetMapping
    public ResponseEntity<List<BateriaRespuestaDto>> listarTodos() {
        return ResponseEntity.ok(bateriaServicio.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BateriaRespuestaDto> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(bateriaServicio.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<BateriaRespuestaDto> guardar(@Valid @RequestBody BateriaSolicitudDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bateriaServicio.guardar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BateriaRespuestaDto> actualizar(@PathVariable Integer id, @Valid @RequestBody BateriaSolicitudDto dto) {
        return ResponseEntity.ok(bateriaServicio.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        bateriaServicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
