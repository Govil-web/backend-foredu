package com.foroescolar.mapper.grado;

import com.foroescolar.dtos.grado.GradoDto;
import com.foroescolar.model.Grado;
import com.foroescolar.model.Profesor;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-06T05:24:54+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.41.0.z20250213-2037, environment: Java 21.0.5 (Microsoft)"
)
@Component
public class GradoMapperImpl extends GradoMapper {

    @Override
    public GradoDto toResponseDto(Grado grado) {
        if ( grado == null ) {
            return null;
        }

        GradoDto.GradoDtoBuilder gradoDto = GradoDto.builder();

        gradoDto.profesor( profesorToLong( grado.getProfesor() ) );
        gradoDto.profesorNombre( gradoProfesorNombre( grado ) );
        gradoDto.aula( grado.getAula() );
        gradoDto.curso( grado.getCurso() );
        gradoDto.id( grado.getId() );
        gradoDto.materia( grado.getMateria() );
        gradoDto.turno( grado.getTurno() );

        return gradoDto.build();
    }

    @Override
    public Grado toEntity(GradoDto gradoDto) {
        if ( gradoDto == null ) {
            return null;
        }

        Grado.GradoBuilder grado = Grado.builder();

        grado.profesor( longToProfesor( gradoDto.getProfesor() ) );
        grado.aula( gradoDto.getAula() );
        grado.curso( gradoDto.getCurso() );
        grado.id( gradoDto.getId() );
        grado.materia( gradoDto.getMateria() );
        grado.turno( gradoDto.getTurno() );

        return grado.build();
    }

    private String gradoProfesorNombre(Grado grado) {
        if ( grado == null ) {
            return null;
        }
        Profesor profesor = grado.getProfesor();
        if ( profesor == null ) {
            return null;
        }
        String nombre = profesor.getNombre();
        if ( nombre == null ) {
            return null;
        }
        return nombre;
    }
}
