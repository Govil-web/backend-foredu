package com.dev.ForoEscolar.mapper.tutorlegal;

import com.dev.ForoEscolar.dtos.tutorlegal.TutorLegalRequestDTO;
import com.dev.ForoEscolar.dtos.tutorlegal.TutorLegalResponseDTO;
import com.dev.ForoEscolar.model.Estudiante;
import com.dev.ForoEscolar.model.TutorLegal;
import com.dev.ForoEscolar.repository.EstudianteRepository;
import com.dev.ForoEscolar.repository.TutorLegalRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {TutorLegalRepository.class, EstudianteRepository.class})
public abstract class TutorLegalMapper {

    @Autowired
    private EstudianteRepository estudianteRepository;




    @Mapping(source = "estudiante", target = "estudiantes", qualifiedByName = "longListToEstudiantes")
   public abstract TutorLegal toEntity(TutorLegalRequestDTO tutorLegalRequestDTO);

    @Mapping(source = "estudiantes", target = "estudiante", qualifiedByName = "estudiantesToLongList")
   public abstract TutorLegalResponseDTO toResponseDTO(TutorLegal tutorLegal);


     @Named("estudiantesToLongList")
     protected List<Long> estudiantesToLongList(List<Estudiante> estudiantes) {

         if (estudiantes == null) {
             return Collections.emptyList();
         }
         return estudiantes.stream().map(estudiante -> estudiante.getId()).toList();


     }

     @Named("longListToEstudiantes")
    protected List<Estudiante> longListToEstudiantes(List<Long> ids) {
        return ids != null ? ids.stream().map(id -> estudianteRepository.findById(id).orElse(null)).collect(Collectors.toList()) : null;
    }
}
