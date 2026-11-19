package autobaterias.demo.dtos.bateria;

import java.math.BigDecimal;

public record BateriaRespuestaDto(
        Integer id,
        String codigo,
        String modelo,
        Integer marcaId,
        String nombreMarca,
        BigDecimal precio,
        Integer mesesGarantia,
        Integer existencias,
        Boolean activo
) {
}
