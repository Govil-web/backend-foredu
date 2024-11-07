package com.dev.ForoEscolar.mapper.estudiante;

import com.dev.ForoEscolar.dtos.estudiante.EstudianteResponseDTO;
import com.dev.ForoEscolar.enums.GeneroEnum;
import com.dev.ForoEscolar.enums.TipoDocumentoEnum;
import com.dev.ForoEscolar.model.Estudiante;
import java.time.LocalDate;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-06T22:36:17-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 22 (Oracle Corporation)"
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
        estudiante.id( estudianteResponseDTO.id() );
        estudiante.nombre( estudianteResponseDTO.nombre() );
        estudiante.apellido( estudianteResponseDTO.apellido() );
        estudiante.dni( estudianteResponseDTO.dni() );
        estudiante.genero( estudianteResponseDTO.genero() );
        estudiante.activo( estudianteResponseDTO.activo() );
        estudiante.fechaNacimiento( estudianteResponseDTO.fechaNacimiento() );
        if ( estudianteResponseDTO.tipoDocumento() != null ) {
            estudiante.tipoDocumento( Enum.valueOf( TipoDocumentoEnum.class, estudianteResponseDTO.tipoDocumento() ) );
        }

        return estudiante.build();
    }
}
