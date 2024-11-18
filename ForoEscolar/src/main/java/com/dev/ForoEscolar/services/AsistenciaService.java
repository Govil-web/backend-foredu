package com.dev.ForoEscolar.services;

import com.dev.ForoEscolar.dtos.asistencia.AsistenciaDTO;
import com.dev.ForoEscolar.dtos.asistencia.AsistenciaRequestDto;

import java.time.LocalDate;
import java.util.List;

public interface AsistenciaService extends GenericServiceDto<Long, AsistenciaDTO>{

    void update(AsistenciaRequestDto asistenciaDTO);

    AsistenciaDTO save(AsistenciaRequestDto requestDTO);

    Iterable<AsistenciaDTO> getAsistenciasByEstudianteID(Long estudianteId);
   Iterable<AsistenciaDTO> getAsistenciasByGradoAndEstudiante(Long estudianteId,Long gradoId);
    Iterable<AsistenciaDTO> getAsistenciasByGrado(Long gradoId);
    List<AsistenciaDTO> getByFechaBeetweenAndGrado(Long gradoId, LocalDate fechaDesde, LocalDate fechaHasta);


}
