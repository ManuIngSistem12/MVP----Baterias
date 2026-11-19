package autobaterias.demo.mapeadores;

import autobaterias.demo.dtos.bateria.BateriaRespuestaDto;
import autobaterias.demo.dtos.bateria.BateriaSolicitudDto;
import autobaterias.demo.entidades.Bateria;
import autobaterias.demo.entidades.Marca;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class BateriaMapeador {

    public BateriaRespuestaDto aRespuesta(Bateria bateria) {
        return new BateriaRespuestaDto(
                bateria.getId(),
                bateria.getCodigo(),
                bateria.getModelo(),
                bateria.getMarca().getId(),
                bateria.getMarca().getNombre(),
                bateria.getPrecio(),
                bateria.getMesesGarantia(),
                bateria.getExistencias(),
                bateria.getActivo());
    }

    public Bateria aEntidad(BateriaSolicitudDto dto, Marca marca) {
        return Bateria.builder()
                .codigo(normalizarCodigo(dto.codigo()))
                .modelo(dto.modelo().trim())
                .marca(marca)
                .precio(dto.precio())
                .mesesGarantia(dto.mesesGarantia())
                .existencias(dto.existencias())
                .build();
    }

    public void actualizarEntidad(Bateria bateria, BateriaSolicitudDto dto, Marca marca) {
        bateria.setCodigo(normalizarCodigo(dto.codigo()));
        bateria.setModelo(dto.modelo().trim());
        bateria.setMarca(marca);
        bateria.setPrecio(dto.precio());
        bateria.setMesesGarantia(dto.mesesGarantia());
        bateria.setExistencias(dto.existencias());
    }

    public String normalizarCodigo(String codigo) {
        return codigo.trim().toUpperCase(Locale.ROOT);
    }
}
