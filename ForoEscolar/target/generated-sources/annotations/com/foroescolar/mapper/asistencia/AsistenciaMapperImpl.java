package com.foroescolar.mapper.asistencia;

import com.foroescolar.dtos.asistencia.AsistenciaDTO;
import com.foroescolar.dtos.asistencia.AsistenciaRequestDto;
import com.foroescolar.enums.EstadoAsistencia;
import com.foroescolar.model.Asistencia;
import com.foroescolar.model.Estudiante;
import com.foroescolar.model.Fecha;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-08T21:13:47-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 22 (Oracle Corporation)"
)
@Component
public class AsistenciaMapperImpl extends AsistenciaMapper {

    @Override
    public Asistencia toEntity(AsistenciaRequestDto asistenciaRquestDto) {
        if ( asistenciaRquestDto == null ) {
            return null;
        }

        Asistencia asistencia = new Asistencia();

        asistencia.setEstudiante( longToEstudiante( asistenciaRquestDto.getEstudiante() ) );
        asistencia.setGrado( longToGrado( asistenciaRquestDto.getGrado() ) );
        asistencia.setObservaciones( asistenciaRquestDto.getJustificativos() );
        asistencia.setId( asistenciaRquestDto.getId() );
        if ( asistenciaRquestDto.getEstado() != null ) {
            asistencia.setEstado( Enum.valueOf( EstadoAsistencia.class, asistenciaRquestDto.getEstado() ) );
        }

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
        asistenciaDTO.fecha( asistenciaFechaFecha( asistencia ) );
        if ( asistencia.getEstado() != null ) {
            asistenciaDTO.estado( asistencia.getEstado().name() );
        }

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

    private LocalDate asistenciaFechaFecha(Asistencia asistencia) {
        if ( asistencia == null ) {
            return null;
        }
        Fecha fecha = asistencia.getFecha();
        if ( fecha == null ) {
            return null;
        }
        LocalDate fecha1 = fecha.getFecha();
        if ( fecha1 == null ) {
            return null;
        }
        return fecha1;
    }
}
