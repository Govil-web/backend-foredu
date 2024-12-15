package com.dev.ForoEscolar.services;

import com.dev.ForoEscolar.dtos.grado.GradoDto;
import com.dev.ForoEscolar.model.Grado;
import com.dev.ForoEscolar.services.GenericServiceDto;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public interface GradoService extends GenericServiceDto<Grado, GradoDto> {
    Iterable<GradoDto> findGradosByProfesorId(Long id);

    Iterable<GradoDto> findGradosByTutorId(Long id);

    boolean hasActiveAssociations(Long id);

    boolean existsById(Long id);

    GradoDto update(@Valid GradoDto gradoDto);
}
