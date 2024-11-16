package com.dev.ForoEscolar.mapper.grado;

import com.dev.ForoEscolar.dtos.grado.GradoDto;
import com.dev.ForoEscolar.model.Grado;
import com.dev.ForoEscolar.model.Profesor;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-16T11:59:16-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 22 (Oracle Corporation)"
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
        gradoDto.id( grado.getId() );
        gradoDto.aula( grado.getAula() );
        gradoDto.curso( grado.getCurso() );
        gradoDto.turno( grado.getTurno() );
        gradoDto.materia( grado.getMateria() );

        return gradoDto.build();
    }

    @Override
    public Grado toEntity(GradoDto gradoDto) {
        if ( gradoDto == null ) {
            return null;
        }

        Grado.GradoBuilder grado = Grado.builder();

        grado.profesor( longToProfesor( gradoDto.getProfesor() ) );
        grado.id( gradoDto.getId() );
        grado.aula( gradoDto.getAula() );
        grado.curso( gradoDto.getCurso() );
        grado.turno( gradoDto.getTurno() );
        grado.materia( gradoDto.getMateria() );

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
