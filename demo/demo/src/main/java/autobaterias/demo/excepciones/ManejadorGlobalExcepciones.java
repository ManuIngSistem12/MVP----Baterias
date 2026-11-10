package autobaterias.demo.excepciones;

import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ManejadorGlobalExcepciones {

    @ExceptionHandler(ExcepcionApi.class)
    public ResponseEntity<RespuestaError> manejarExcepcionApi(ExcepcionApi ex) {
        return construir(ex.getEstado(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaError> manejarValidacion(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining("; "));
        return construir(HttpStatus.BAD_REQUEST, mensaje);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<RespuestaError> manejarSolicitudInvalida(Exception ex) {
        return construir(HttpStatus.BAD_REQUEST, "Solicitud mal formada");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<RespuestaError> manejarIntegridadDatos(DataIntegrityViolationException ex) {
        return construir(HttpStatus.CONFLICT, "La operación viola una restricción de integridad de datos");
    }

    private ResponseEntity<RespuestaError> construir(HttpStatus estado, String mensaje) {
        return ResponseEntity.status(estado).body(new RespuestaError(estado.value(), mensaje));
    }
}
