package autobaterias.demo.excepciones;

import org.springframework.http.HttpStatus;

public class ConflictoExcepcion extends ExcepcionApi {

    public ConflictoExcepcion(String mensaje) {
        super(HttpStatus.CONFLICT, mensaje);
    }
}
