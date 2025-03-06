package com.foroescolar.mapper.asistencia;

import com.foroescolar.dtos.asistencia.AsistenciaDTO;
import com.foroescolar.dtos.asistencia.AsistenciaRequestDto;
import com.foroescolar.model.Asistencia;
import com.foroescolar.model.Estudiante;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-06T05:24:54+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.41.0.z20250213-2037, environment: Java 21.0.5 (Microsoft)"
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
        asistencia.setAsistio( asistenciaRquestDto.isAsistio() );
        asistencia.setFecha( asistenciaRquestDto.getFecha() );
        asistencia.setId( asistenciaRquestDto.getId() );

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
        asistenciaDTO.asistenciaAlumno( asistencia.getAsistenciaAlumno() );
        asistenciaDTO.asistio( asistencia.isAsistio() );

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
