package com.dev.ForoEscolar.services;

import com.dev.ForoEscolar.dtos.asistencia.AsistenciaDTO;
import com.dev.ForoEscolar.dtos.estudiante.EstudianteResponseDTO;
import com.dev.ForoEscolar.model.Estudiante;


import java.util.List;

public interface EstudianteService extends GenericServiceDto<Long, EstudianteResponseDTO>{

    EstudianteResponseDTO update(EstudianteResponseDTO estudianteRequestDTO);

    List<EstudianteResponseDTO> findByGradoId(Long gradoId);

    List<AsistenciaDTO> findByEstudianteId(Long id);

    boolean subscribe_unsubscribe(Long id);

}
