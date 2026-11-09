package autobaterias.demo.excepciones;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ExcepcionApi extends RuntimeException {

    private final HttpStatus estado;

    protected ExcepcionApi(HttpStatus estado, String mensaje) {
        super(mensaje);
        this.estado = estado;
    }
}
