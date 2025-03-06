package com.foroescolar.mapper.profesor;

import com.foroescolar.dtos.profesor.ProfesorRequestDTO;
import com.foroescolar.dtos.profesor.ProfesorResponseDTO;
import com.foroescolar.enums.MateriaEnum;
import com.foroescolar.enums.TipoDocumentoEnum;
import com.foroescolar.model.Profesor;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-06T05:24:54+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.41.0.z20250213-2037, environment: Java 21.0.5 (Microsoft)"
)
@Component
public class ProfesorMapperImpl extends ProfesorMapper {

    @Override
    public ProfesorResponseDTO toResponseDTO(Profesor profesor) {
        if ( profesor == null ) {
            return null;
        }

        List<Long> estudianteIds = null;
        List<Long> boletinIds = null;
        List<Long> asistenciaIds = null;
        List<Long> tareaIds = null;
        List<Long> calificacionIds = null;
        List<Long> gradoIds = null;
        Long id = null;
        String email = null;
        String nombre = null;
        String tipoDocumento = null;
        String dni = null;
        String apellido = null;
        String telefono = null;
        String institucion = null;
        String rol = null;
        boolean activo = false;
        String materia = null;

        estudianteIds = estudiantesToLongList( profesor.getEstudiantes() );
        boletinIds = boletinesToLongList( profesor.getBoletin() );
        asistenciaIds = asistenciasToLongList( profesor.getAsistencia() );
        tareaIds = tareasToLongList( profesor.getTarea() );
        calificacionIds = calificacionesToLongList( profesor.getCalificaciones() );
        gradoIds = gradosToLongList( profesor.getGrado() );
        id = profesor.getId();
        email = profesor.getEmail();
        nombre = profesor.getNombre();
        if ( profesor.getTipoDocumento() != null ) {
            tipoDocumento = profesor.getTipoDocumento().name();
        }
        dni = profesor.getDni();
        apellido = profesor.getApellido();
        telefono = profesor.getTelefono();
        institucion = profesor.getInstitucion();
        if ( profesor.getRol() != null ) {
            rol = profesor.getRol().name();
        }
        activo = profesor.isActivo();
        if ( profesor.getMateria() != null ) {
            materia = profesor.getMateria().name();
        }

        ProfesorResponseDTO profesorResponseDTO = new ProfesorResponseDTO( id, email, nombre, tipoDocumento, dni, apellido, telefono, institucion, rol, activo, materia, estudianteIds, boletinIds, asistenciaIds, tareaIds, calificacionIds, gradoIds );

        return profesorResponseDTO;
    }

    @Override
    public Profesor toEntity(ProfesorRequestDTO profesorRequestDTO) {
        if ( profesorRequestDTO == null ) {
            return null;
        }

        Profesor profesor = new Profesor();

        profesor.setId( profesorRequestDTO.id() );
        profesor.setEstudiantes( longListToEstudiantes( profesorRequestDTO.estudianteIds() ) );
        profesor.setBoletin( longListToBoletines( profesorRequestDTO.boletinIds() ) );
        profesor.setAsistencia( longListToAsistencias( profesorRequestDTO.asistenciaIds() ) );
        profesor.setTarea( longListToTareas( profesorRequestDTO.tareaIds() ) );
        profesor.setCalificaciones( longListToCalificaciones( profesorRequestDTO.calificacionIds() ) );
        profesor.setGrado( longListToGrados( profesorRequestDTO.gradoIds() ) );
        profesor.setApellido( profesorRequestDTO.apellido() );
        profesor.setContrasena( profesorRequestDTO.contrasena() );
        profesor.setDni( profesorRequestDTO.dni() );
        profesor.setEmail( profesorRequestDTO.email() );
        profesor.setInstitucion( profesorRequestDTO.institucion() );
        profesor.setNombre( profesorRequestDTO.nombre() );
        profesor.setTelefono( profesorRequestDTO.telefono() );
        if ( profesorRequestDTO.tipoDocumento() != null ) {
            profesor.setTipoDocumento( Enum.valueOf( TipoDocumentoEnum.class, profesorRequestDTO.tipoDocumento() ) );
        }
        if ( profesorRequestDTO.materia() != null ) {
            profesor.setMateria( Enum.valueOf( MateriaEnum.class, profesorRequestDTO.materia() ) );
        }

        return profesor;
    }
}
