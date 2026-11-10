package autobaterias.demo.excepciones;

import org.springframework.http.HttpStatus;

public class RecursoNoEncontradoExcepcion extends ExcepcionApi {

    public RecursoNoEncontradoExcepcion(String recurso, Object id) {
        super(HttpStatus.NOT_FOUND, "%s con id %s no encontrado".formatted(recurso, id));
    }
}
