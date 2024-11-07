package com.dev.ForoEscolar.mapper.asistencia;

import com.dev.ForoEscolar.dtos.asistencia.AsistenciaDTO;
import com.dev.ForoEscolar.model.Asistencia;
import com.dev.ForoEscolar.model.Estudiante;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-06T21:14:28-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class AsistenciaMapperImpl extends AsistenciaMapper {

    @Override
    public Asistencia toEntity(AsistenciaDTO asistenciaDTO) {
        if ( asistenciaDTO == null ) {
            return null;
        }

        Asistencia asistencia = new Asistencia();

        asistencia.setProfesor( longToProfesor( asistenciaDTO.getProfesor() ) );
        asistencia.setEstudiante( longToEstudiante( asistenciaDTO.getEstudiante() ) );
        asistencia.setGrado( longToGrado( asistenciaDTO.getGrado() ) );
        asistencia.setId( asistenciaDTO.getId() );
        asistencia.setAsistio( asistenciaDTO.isAsistio() );
        asistencia.setContadorClases( asistenciaDTO.isContadorClases() );
        asistencia.setContadorTotal( asistenciaDTO.getContadorTotal() );
        asistencia.setAsistenciaAlumno( asistenciaDTO.getAsistenciaAlumno() );
        asistencia.setFecha( asistenciaDTO.getFecha() );
        asistencia.setObservaciones( asistenciaDTO.getObservaciones() );

        return asistencia;
    }

    @Override
    public AsistenciaDTO toResponseDto(Asistencia asistencia) {
        if ( asistencia == null ) {
            return null;
        }

        AsistenciaDTO.AsistenciaDTOBuilder asistenciaDTO = AsistenciaDTO.builder();

        asistenciaDTO.porcentajeAsistencia( calcular( asistenciaEstudianteId( asistencia ) ) );
        asistenciaDTO.profesor( profesorToLong( asistencia.getProfesor() ) );
        asistenciaDTO.estudiante( estudianteToLong( asistencia.getEstudiante() ) );
        asistenciaDTO.grado( gradoToLong( asistencia.getGrado() ) );
        asistenciaDTO.id( asistencia.getId() );
        asistenciaDTO.asistio( asistencia.isAsistio() );
        asistenciaDTO.contadorClases( asistencia.isContadorClases() );
        asistenciaDTO.contadorTotal( asistencia.getContadorTotal() );
        asistenciaDTO.asistenciaAlumno( asistencia.getAsistenciaAlumno() );
        asistenciaDTO.fecha( asistencia.getFecha() );
        asistenciaDTO.observaciones( asistencia.getObservaciones() );

        return asistenciaDTO.build();
    }

    private Long asistenciaEstudianteId(Asistencia asistencia) {
        if ( asistencia == null ) {
            return null;
        }
        Estudiante estudiante = asistencia.getEstudiante();
        if ( estudiante == null ) {
            return null;
        }
        Long id = estudiante.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
