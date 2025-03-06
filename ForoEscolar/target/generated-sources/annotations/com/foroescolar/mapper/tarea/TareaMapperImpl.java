package com.foroescolar.mapper.tarea;

import com.foroescolar.dtos.tarea.TareaResponseDto;
import com.foroescolar.model.Tarea;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-06T05:24:54+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.41.0.z20250213-2037, environment: Java 21.0.5 (Microsoft)"
)
@Component
public class TareaMapperImpl extends TareaMapper {

    @Override
    public TareaResponseDto toResponseDTO(Tarea tarea) {
        if ( tarea == null ) {
            return null;
        }

        TareaResponseDto.TareaResponseDtoBuilder tareaResponseDto = TareaResponseDto.builder();

        tareaResponseDto.profesor( profesorToLong( tarea.getProfesor() ) );
        tareaResponseDto.estudiante( estudianteToLong( tarea.getEstudiante() ) );
        tareaResponseDto.activo( tarea.isActivo() );
        tareaResponseDto.descripcion( tarea.getDescripcion() );
        tareaResponseDto.estadoDeEntrega( tarea.getEstadoDeEntrega() );
        tareaResponseDto.fechaEntrega( tarea.getFechaEntrega() );
        tareaResponseDto.id( tarea.getId() );
        tareaResponseDto.titulo( tarea.getTitulo() );

        return tareaResponseDto.build();
    }

    @Override
    public Tarea toEntity(TareaResponseDto tareaRequestDTO) {
        if ( tareaRequestDTO == null ) {
            return null;
        }

        Tarea.TareaBuilder tarea = Tarea.builder();

        tarea.profesor( longToProfesor( tareaRequestDTO.getProfesor() ) );
        tarea.estudiante( longToEstudiante( tareaRequestDTO.getEstudiante() ) );
        tarea.activo( tareaRequestDTO.isActivo() );
        tarea.descripcion( tareaRequestDTO.getDescripcion() );
        tarea.estadoDeEntrega( tareaRequestDTO.getEstadoDeEntrega() );
        tarea.fechaEntrega( tareaRequestDTO.getFechaEntrega() );
        tarea.id( tareaRequestDTO.getId() );
        tarea.titulo( tareaRequestDTO.getTitulo() );

        return tarea.build();
    }
}
