package autobaterias.demo.services.Impl;

import autobaterias.demo.dtos.rol.RolRequestDto;
import autobaterias.demo.dtos.rol.RolResponseDto;
import autobaterias.demo.entities.Rol;
import autobaterias.demo.exceptions.ConflictException;
import autobaterias.demo.exceptions.ResourceNotFoundException;
import autobaterias.demo.mappers.RolMapper;
import autobaterias.demo.repositories.IRolRepository;
import autobaterias.demo.services.IRolService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RolServiceImpl implements IRolService {

    private static final String RESOURCE = "Rol";

    private final IRolRepository rolRepository;
    private final RolMapper rolMapper;

    @Override
    public List<RolResponseDto> findAll() {
        return rolRepository.findAll().stream()
                .map(rolMapper::toResponse)
                .toList();
    }

    @Override
    public RolResponseDto findById(Integer id) {
        return rolMapper.toResponse(getRol(id));
    }

    @Override
    @Transactional
    public RolResponseDto save(RolRequestDto dto) {
        String name = rolMapper.normalizeName(dto.name());
        if (rolRepository.existsByNameIgnoreCase(name)) {
            throw new ConflictException("El rol '%s' ya existe".formatted(name));
        }
        return rolMapper.toResponse(rolRepository.save(rolMapper.toEntity(dto)));
    }

    @Override
    @Transactional
    public RolResponseDto update(Integer id, RolRequestDto dto) {
        Rol rol = getRol(id);
        String name = rolMapper.normalizeName(dto.name());
        if (rolRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new ConflictException("El rol '%s' ya existe".formatted(name));
        }
        rolMapper.updateEntity(rol, dto);
        return rolMapper.toResponse(rol);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        rolRepository.delete(getRol(id));
    }

    private Rol getRol(Integer id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE, id));
    }
}
