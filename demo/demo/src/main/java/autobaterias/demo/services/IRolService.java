package autobaterias.demo.services;

import autobaterias.demo.dtos.rol.RolRequestDto;
import autobaterias.demo.dtos.rol.RolResponseDto;
import java.util.List;

public interface IRolService {

    List<RolResponseDto> findAll();

    RolResponseDto findById(Integer id);

    RolResponseDto save(RolRequestDto dto);

    RolResponseDto update(Integer id, RolRequestDto dto);

    void delete(Integer id);
}
