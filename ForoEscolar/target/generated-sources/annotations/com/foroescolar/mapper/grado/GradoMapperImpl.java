package com.foroescolar.mapper.grado;

import com.foroescolar.dtos.grado.GradoDto;
import com.foroescolar.model.Grado;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-02T18:22:26-0300",
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
        gradoDto.profesorNombre( profesorName( grado.getProfesor() ) );
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
}
