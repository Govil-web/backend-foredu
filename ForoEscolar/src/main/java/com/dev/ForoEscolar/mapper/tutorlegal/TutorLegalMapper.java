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


import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {TutorLegalRepository.class, EstudianteRepository.class})
public abstract class TutorLegalMapper {

    @Autowired
    private EstudianteRepository estudianteRepository;




    @Mapping(source = "estudiante", target = "estudiante", qualifiedByName = "longListToEstudiante")
   public abstract TutorLegal toEntity(TutorLegalRequestDTO tutorLegalRequestDTO);

    @Mapping(source = "estudiante", target = "estudiante", qualifiedByName = "estudianteToLongList")
   public abstract TutorLegalResponseDTO toResponseDTO(TutorLegal tutorLegal);


    @Named("estudianteToLongList")
    public List<Long> estudianteToLongList(List<Estudiante> estudiantes) {
        return estudiantes.stream()
                .filter(estudiante -> estudiante != null && estudiante.getId() != null)
                .map(Estudiante::getId)
                .collect(Collectors.toList());
    }

     @Named("longListToEstudiante")
    protected List<Estudiante> longListToEstudiante(List<Long> ids) {
        return ids != null ? ids.stream().map(id -> estudianteRepository.findById(id).orElse(null)).collect(Collectors.toList()) : null;
    }
}
