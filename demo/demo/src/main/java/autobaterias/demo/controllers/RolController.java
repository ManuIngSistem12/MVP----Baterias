package autobaterias.demo.controllers;

import autobaterias.demo.dtos.rol.RolRequestDto;
import autobaterias.demo.dtos.rol.RolResponseDto;
import autobaterias.demo.services.IRolService;
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
public class RolController {

    private final IRolService rolService;

    @GetMapping
    public ResponseEntity<List<RolResponseDto>> findAll() {
        return ResponseEntity.ok(rolService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolResponseDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(rolService.findById(id));
    }

    @PostMapping
    public ResponseEntity<RolResponseDto> save(@Valid @RequestBody RolRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rolService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RolResponseDto> update(@PathVariable Integer id, @Valid @RequestBody RolRequestDto dto) {
        return ResponseEntity.ok(rolService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        rolService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
