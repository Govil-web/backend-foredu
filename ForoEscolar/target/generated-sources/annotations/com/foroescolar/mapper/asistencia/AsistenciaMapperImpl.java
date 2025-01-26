package com.foroescolar.mapper.asistencia;

import com.foroescolar.dtos.asistencia.AsistenciaDTO;
import com.foroescolar.dtos.asistencia.AsistenciaRequestDto;
import com.foroescolar.model.Asistencia;
import com.foroescolar.model.Estudiante;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-01-25T14:18:41-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class AsistenciaMapperImpl extends AsistenciaMapper {

    @Override
    public Asistencia toEntity(AsistenciaRequestDto asistenciaRquestDto) {
        if ( asistenciaRquestDto == null ) {
            return null;
        }

        Asistencia asistencia = new Asistencia();

        asistencia.setProfesor( longToProfesor( asistenciaRquestDto.getProfesor() ) );
        asistencia.setEstudiante( longToEstudiante( asistenciaRquestDto.getEstudiante() ) );
        asistencia.setGrado( longToGrado( asistenciaRquestDto.getGrado() ) );
        asistencia.setObservaciones( asistenciaRquestDto.getJustificativos() );
        asistencia.setId( asistenciaRquestDto.getId() );
        asistencia.setAsistio( asistenciaRquestDto.isAsistio() );
        asistencia.setFecha( asistenciaRquestDto.getFecha() );

        return asistencia;
    }

    @Override
    public AsistenciaDTO toResponseDto(Asistencia asistencia) {
        if ( asistencia == null ) {
            return null;
        }

        AsistenciaDTO.AsistenciaDTOBuilder asistenciaDTO = AsistenciaDTO.builder();

        asistenciaDTO.id( asistencia.getId() );
        asistenciaDTO.porcentajeAsistencia( calcularPorcentaje( asistenciaEstudianteId( asistencia ) ) );
        asistenciaDTO.justificativos( asistencia.getObservaciones() );
        asistenciaDTO.nombreEstudiante( estudianteName( asistencia.getEstudiante() ) );
        asistenciaDTO.estudiante( estudianteToLong( asistencia.getEstudiante() ) );
        asistenciaDTO.grado( gradoToLong( asistencia.getGrado() ) );
        asistenciaDTO.fecha( asistencia.getFecha() );
        asistenciaDTO.asistio( asistencia.isAsistio() );
        asistenciaDTO.asistenciaAlumno( asistencia.getAsistenciaAlumno() );

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
