package com.foroescolar.mapper.calificacion;

import com.foroescolar.dtos.calificacion.CalificacionDTO;
import com.foroescolar.model.Calificacion;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-06T05:24:54+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.41.0.z20250213-2037, environment: Java 21.0.5 (Microsoft)"
)
@Component
public class CalificacionMapperImpl extends CalificacionMapper {

    @Override
    public Calificacion toEntity(CalificacionDTO calificacionDTO) {
        if ( calificacionDTO == null ) {
            return null;
        }

        Calificacion.CalificacionBuilder calificacion = Calificacion.builder();

        calificacion.estudiante( longToEstudiante( calificacionDTO.getEstudiante() ) );
        calificacion.profesor( longToProfesor( calificacionDTO.getProfesor() ) );
        calificacion.boletin( longToBoletin( calificacionDTO.getBoletin() ) );
        calificacion.comentario( calificacionDTO.getComentario() );
        calificacion.fecha( calificacionDTO.getFecha() );
        calificacion.id( calificacionDTO.getId() );
        calificacion.materia( calificacionDTO.getMateria() );
        calificacion.nota( calificacionDTO.getNota() );
        calificacion.periodo( calificacionDTO.getPeriodo() );

        return calificacion.build();
    }

    @Override
    public CalificacionDTO toResponseDto(Calificacion calificacion) {
        if ( calificacion == null ) {
            return null;
        }

        CalificacionDTO.CalificacionDTOBuilder calificacionDTO = CalificacionDTO.builder();

        calificacionDTO.estudiante( estudianteToLong( calificacion.getEstudiante() ) );
        calificacionDTO.profesor( profesorToLong( calificacion.getProfesor() ) );
        calificacionDTO.boletin( boletinToLong( calificacion.getBoletin() ) );
        calificacionDTO.comentario( calificacion.getComentario() );
        calificacionDTO.fecha( calificacion.getFecha() );
        calificacionDTO.id( calificacion.getId() );
        calificacionDTO.materia( calificacion.getMateria() );
        calificacionDTO.nota( calificacion.getNota() );
        calificacionDTO.periodo( calificacion.getPeriodo() );

        return calificacionDTO.build();
    }
}
