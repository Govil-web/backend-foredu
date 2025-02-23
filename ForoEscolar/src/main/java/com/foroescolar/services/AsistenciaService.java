package com.foroescolar.services;

import com.foroescolar.dtos.asistencia.AsistenciaDTO;
import com.foroescolar.dtos.asistencia.AsistenciaRequest;
import com.foroescolar.dtos.asistencia.AsistenciaRequestDto;
import com.foroescolar.dtos.asistencia.DetalleAsistenciaByAlumno;

import java.time.LocalDate;
import java.util.List;

public interface AsistenciaService extends GenericServiceDto<Long, AsistenciaDTO>{

    void update(AsistenciaRequestDto asistenciaDTO);

   void asistenciaDelDia(AsistenciaRequest requestDTO);

    Iterable<AsistenciaDTO> getAsistenciasByEstudianteID(Long estudianteId);
   Iterable<AsistenciaDTO> getAsistenciasByGradoAndEstudiante(Long estudianteId,Long gradoId);
    Iterable<AsistenciaDTO> getAsistenciasByGrado(Long gradoId);
    List<AsistenciaDTO> getByFechaBeetweenAndGrado(Long gradoId, LocalDate fechaDesde, LocalDate fechaHasta);


    DetalleAsistenciaByAlumno findByAsistenciaPresente(Long estudianteId);
}
