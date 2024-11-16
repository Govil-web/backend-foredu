package com.dev.ForoEscolar.mapper.asistencia;

import com.dev.ForoEscolar.dtos.asistencia.AsistenciaDTO;
import com.dev.ForoEscolar.dtos.asistencia.AsistenciaRequestDto;
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

import java.util.Optional;

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
    @Mapping(source = "justificativos", target = "observaciones")
    public abstract Asistencia toEntity(AsistenciaRequestDto asistenciaRquestDto);


//    @Mapping(source = "estudiante", target = "nombreEstudiante", qualifiedByName = "estudianteName")
//    @Mapping(source = "profesor", target = "profesor", qualifiedByName = "profesorToLong")
//    @Mapping(source = "estudiante", target = "estudiante", qualifiedByName = "estudianteToLong")
//    @Mapping(source = "grado", target = "grado", qualifiedByName = "gradoToLong")
//    @Mapping(source = "observaciones", target = "justificativos")
//    public abstract AsistenciaDTO toResponseDto(Asistencia asistencia);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "estudiante.id", target = "porcentajeAsistencia", qualifiedByName = "calcularPorcentaje")
    @Mapping(source = "observaciones", target = "justificativos")
    @Mapping(source = "estudiante", target = "nombreEstudiante", qualifiedByName = "estudianteName")
    @Mapping(source = "estudiante", target = "estudiante", qualifiedByName = "estudianteToLong")
    @Mapping(source = "grado", target = "grado", qualifiedByName = "gradoToLong")
    @Mapping(source = "fecha", target = "fecha")
    @Mapping(source = "profesor", target = "profesor", ignore = true)
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

    @Named("estudianteName")
    protected String estudianteName(Estudiante estudiante) {
        return estudiante != null ? estudiante.getNombre() + " " + estudiante.getApellido() : null;
    }

    @Named("calcularPorcentaje")
    protected Double calcularPorcentaje(Long estudianteId) {
        long diasAsistidos = asistenciaRepository.countByEstudianteIdAndAsistioTrue(estudianteId);
        long totalClases = obtenerContadorGrado(estudianteId);
       return (double) ((diasAsistidos * 100 )/ totalClases);
    }

    private long obtenerContadorGrado(Long estudianteId) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId).orElse(null);
        Grado grado = gradoRepository.findById(estudiante.getGrado().getId()).orElse(null);
        return grado.getContador();
    }

}

