package com.foroescolar.mapper.boletin;

import com.foroescolar.dtos.boletin.BoletinDto;
import com.foroescolar.model.Boletin;
import com.foroescolar.model.Calificacion;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-06T05:24:54+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.41.0.z20250213-2037, environment: Java 21.0.5 (Microsoft)"
)
@Component
public class BoletinMapperImpl extends BoletinMapper {

    @Override
    public BoletinDto toResponseDto(Boletin boletin) {
        if ( boletin == null ) {
            return null;
        }

        BoletinDto.BoletinDtoBuilder boletinDto = BoletinDto.builder();

        boletinDto.estudiante( estudianteToLong( boletin.getEstudiante() ) );
        boletinDto.calificacion( calificacionListToLongList( boletin.getCalificacion() ) );
        boletinDto.curso( boletin.getCurso() );
        boletinDto.fecha( boletin.getFecha() );
        boletinDto.id( boletin.getId() );
        boletinDto.observaciones( boletin.getObservaciones() );
        boletinDto.periodo( boletin.getPeriodo() );
        boletinDto.promedio( boletin.getPromedio() );

        return boletinDto.build();
    }

    @Override
    public Boletin toEntity(BoletinDto boletinDto) {
        if ( boletinDto == null ) {
            return null;
        }

        Boletin.BoletinBuilder boletin = Boletin.builder();

        boletin.estudiante( longToEstudiante( boletinDto.getEstudiante() ) );
        boletin.calificacion( longListToCalificacionList( boletinDto.getCalificacion() ) );
        boletin.curso( boletinDto.getCurso() );
        boletin.fecha( boletinDto.getFecha() );
        boletin.id( boletinDto.getId() );
        boletin.observaciones( boletinDto.getObservaciones() );
        boletin.periodo( boletinDto.getPeriodo() );
        boletin.promedio( boletinDto.getPromedio() );

        return boletin.build();
    }

    protected List<Long> calificacionListToLongList(List<Calificacion> list) {
        if ( list == null ) {
            return null;
        }

        List<Long> list1 = new ArrayList<Long>( list.size() );
        for ( Calificacion calificacion : list ) {
            list1.add( calificacionToLong( calificacion ) );
        }

        return list1;
    }

    protected List<Calificacion> longListToCalificacionList(List<Long> list) {
        if ( list == null ) {
            return null;
        }

        List<Calificacion> list1 = new ArrayList<Calificacion>( list.size() );
        for ( Long long1 : list ) {
            list1.add( longToCalificacion( long1 ) );
        }

        return list1;
    }
}
