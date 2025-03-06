package com.foroescolar.mapper.estudiante;

import com.foroescolar.dtos.estudiante.EstudianteResponseDTO;
import com.foroescolar.enums.GeneroEnum;
import com.foroescolar.enums.TipoDocumentoEnum;
import com.foroescolar.model.Estudiante;
import java.time.LocalDate;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-06T05:24:54+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.41.0.z20250213-2037, environment: Java 21.0.5 (Microsoft)"
)
@Component
public class EstudianteMapperImpl extends EstudianteMapper {

    @Override
    public EstudianteResponseDTO toResponseDTO(Estudiante estudiante) {
        if ( estudiante == null ) {
            return null;
        }

        Long tutor = null;
        List<Long> boletin = null;
        List<Long> asistencia = null;
        List<Long> tarea = null;
        List<Long> calificaciones = null;
        Long grado = null;
        Long id = null;
        String nombre = null;
        String apellido = null;
        String dni = null;
        GeneroEnum genero = null;
        LocalDate fechaNacimiento = null;
        String tipoDocumento = null;
        Boolean activo = null;

        tutor = tutorToLong( estudiante.getTutor() );
        boletin = boletinesToLongList( estudiante.getBoletin() );
        asistencia = asistenciasToLongList( estudiante.getAsistencia() );
        tarea = tareasToLongList( estudiante.getTarea() );
        calificaciones = calificacionesToLongList( estudiante.getCalificaciones() );
        grado = gradoToLong( estudiante.getGrado() );
        id = estudiante.getId();
        nombre = estudiante.getNombre();
        apellido = estudiante.getApellido();
        dni = estudiante.getDni();
        genero = estudiante.getGenero();
        fechaNacimiento = estudiante.getFechaNacimiento();
        if ( estudiante.getTipoDocumento() != null ) {
            tipoDocumento = estudiante.getTipoDocumento().name();
        }
        activo = estudiante.getActivo();

        EstudianteResponseDTO estudianteResponseDTO = new EstudianteResponseDTO( id, nombre, apellido, dni, genero, fechaNacimiento, tipoDocumento, activo, tutor, grado, asistencia, boletin, tarea, calificaciones );

        return estudianteResponseDTO;
    }

    @Override
    public Estudiante toEntity(EstudianteResponseDTO estudianteResponseDTO) {
        if ( estudianteResponseDTO == null ) {
            return null;
        }

        Estudiante.EstudianteBuilder estudiante = Estudiante.builder();

        estudiante.tutor( longToTutor( estudianteResponseDTO.tutor() ) );
        estudiante.boletin( longListToBoletines( estudianteResponseDTO.boletin() ) );
        estudiante.asistencia( longListToAsistencias( estudianteResponseDTO.asistencia() ) );
        estudiante.tarea( longListToTareas( estudianteResponseDTO.tarea() ) );
        estudiante.calificaciones( longListToCalificaciones( estudianteResponseDTO.calificaciones() ) );
        estudiante.grado( longToGrado( estudianteResponseDTO.grado() ) );
        estudiante.activo( estudianteResponseDTO.activo() );
        estudiante.apellido( estudianteResponseDTO.apellido() );
        estudiante.dni( estudianteResponseDTO.dni() );
        estudiante.fechaNacimiento( estudianteResponseDTO.fechaNacimiento() );
        estudiante.genero( estudianteResponseDTO.genero() );
        estudiante.id( estudianteResponseDTO.id() );
        estudiante.nombre( estudianteResponseDTO.nombre() );
        if ( estudianteResponseDTO.tipoDocumento() != null ) {
            estudiante.tipoDocumento( Enum.valueOf( TipoDocumentoEnum.class, estudianteResponseDTO.tipoDocumento() ) );
        }

        return estudiante.build();
    }
}
