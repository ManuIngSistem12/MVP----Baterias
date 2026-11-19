package autobaterias.demo.dtos.bateria;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record BateriaSolicitudDto(
        @NotBlank @Size(max = 50) String codigo,
        @NotBlank @Size(max = 100) String modelo,
        @NotNull @Positive Integer marcaId,
        @NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal precio,
        @NotNull @PositiveOrZero Integer mesesGarantia,
        @NotNull @PositiveOrZero Integer existencias
) {
}
