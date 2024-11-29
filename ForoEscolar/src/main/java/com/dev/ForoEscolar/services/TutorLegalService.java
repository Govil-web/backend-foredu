package com.dev.ForoEscolar.services;

import com.dev.ForoEscolar.dtos.asistencia.AsistenciaDTO;
import com.dev.ForoEscolar.dtos.tutorlegal.TutorLegalRequestDTO;
import com.dev.ForoEscolar.dtos.tutorlegal.TutorLegalResponseDTO;
import com.dev.ForoEscolar.model.TutorLegal;

import java.util.List;

public interface TutorLegalService extends GenericService<TutorLegal, Long, TutorLegalRequestDTO,TutorLegalResponseDTO>{

    TutorLegalResponseDTO update(TutorLegalRequestDTO tutorLegalRequestDTO);

   Iterable<AsistenciaDTO> findAsistenciasByEstudianteId(Long idEstudiante, Long idGrado);

    boolean hasActiveStudents(Long id);
}
