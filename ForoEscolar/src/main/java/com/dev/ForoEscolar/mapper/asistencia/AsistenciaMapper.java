package com.dev.ForoEscolar.mapper.asistencia;

import com.dev.ForoEscolar.dtos.asistencia.AsistenciaDTO;
import com.dev.ForoEscolar.model.Asistencia;
import com.dev.ForoEscolar.model.Estudiante;
import com.dev.ForoEscolar.model.Grado;
import com.dev.ForoEscolar.model.Profesor;
import com.dev.ForoEscolar.repository.AsistenciaRepository;
import com.dev.ForoEscolar.repository.EstudianteRepository;
import com.dev.ForoEscolar.repository.GradoRepository;
import com.dev.ForoEscolar.repository.ProfesorRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class AsistenciaMapper {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private ProfesorRepository profesorRepository;
    @Autowired
    private AsistenciaRepository asistenciaRepository;
    @Autowired
    private GradoRepository gradoRepository;

    @Mapping(source = "profesor", target = "profesor", qualifiedByName = "longToProfesor")
    @Mapping(source = "estudiante", target = "estudiante", qualifiedByName = "longToEstudiante")
    @Mapping(source = "grado", target = "grado", qualifiedByName = "longToGrado")
    public abstract Asistencia toEntity(AsistenciaDTO asistenciaDTO);

    @Mapping(source="estudiante.id", target = "porcentajeAsistencia", qualifiedByName = "calcularPorcentaje") // expression = "//expression = "java(calcularPorcentaje(asistencia.getEstudianteId(), totalClases))")
    @Mapping(source = "profesor", target = "profesor", qualifiedByName = "profesorToLong")
    @Mapping(source = "estudiante", target = "estudiante", qualifiedByName = "estudianteToLong")
    @Mapping(source = "grado", target = "grado", qualifiedByName = "gradoToLong")
    public abstract AsistenciaDTO toResponseDto(Asistencia asistencia);

    @Named("longToProfesor")
    protected Profesor longToProfesor(Long id) {
        return id != null ? profesorRepository.findById(id).orElse(null) : null;
    }

    @Named("longToEstudiante")
    protected Estudiante longToEstudiante(Long id) {
        return id != null ? estudianteRepository.findById(id).orElse(null) : null;
    }
    @Named("longToGrado")
    protected Grado longToGrado(Long id) {
        return id != null ? gradoRepository.findById(id).orElse(null) : null;
    }

    @Named("profesorToLong")
    protected Long profesorToLong(Profesor profesor) {
        return profesor != null ? profesor.getId() : null;
    }

    @Named("estudianteToLong")
    protected Long estudianteToLong(Estudiante estudiante) {
        return estudiante != null ? estudiante.getId() : null;
    }
    @Named("gradoToLong")
    protected Long gradoToLong(Grado grado) {
        return grado != null ? grado.getId() : null;
    }

    @Named("calcularPorcentaje")
    protected double calcular(Long estudianteId) {
        long diasAsistidos= asistenciaRepository.countByEstudianteIdAndAsistioTrue(estudianteId);
        long diasDeClases= asistenciaRepository.countByContadorClases(true);
        return (double) ((diasAsistidos*100) / diasDeClases);
    }



}
