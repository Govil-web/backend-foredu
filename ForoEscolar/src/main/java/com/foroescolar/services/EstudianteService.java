package com.foroescolar.services;

import com.foroescolar.dtos.asistencia.AsistenciaDTO;
import com.foroescolar.dtos.estudiante.EstudiantePerfilDto;
import com.foroescolar.dtos.estudiante.EstudianteResponseDTO;
import com.foroescolar.model.Estudiante;


import java.util.List;

public interface EstudianteService extends GenericServiceDto<Long, EstudianteResponseDTO>{

    List<EstudiantePerfilDto> findAllStudents();

    EstudianteResponseDTO update(EstudianteResponseDTO estudianteRequestDTO);

    List<EstudiantePerfilDto> findByGradoId(Long gradoId);

    List<AsistenciaDTO> findByEstudianteId(Long id);

    boolean subscribe_unsubscribe(Long id);

    List<Estudiante> findByIds(List<Long> id);

    Estudiante findByIdToEntity(Long id);
}
