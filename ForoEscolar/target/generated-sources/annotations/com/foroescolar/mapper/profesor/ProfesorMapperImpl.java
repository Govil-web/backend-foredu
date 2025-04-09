package com.foroescolar.mapper.profesor;

import com.foroescolar.dtos.profesor.ProfesorRequestDTO;
import com.foroescolar.dtos.profesor.ProfesorResponseDTO;
import com.foroescolar.enums.MateriaEnum;
import com.foroescolar.enums.TipoDocumentoEnum;
import com.foroescolar.model.Institucion;
import com.foroescolar.model.Profesor;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-08T22:10:48-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 22 (Oracle Corporation)"
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
        List<Long> tareaIds = null;
        List<Long> calificacionIds = null;
        List<Long> gradoIds = null;
        String institucion = null;
        Long id = null;
        String email = null;
        String nombre = null;
        String tipoDocumento = null;
        String dni = null;
        String apellido = null;
        String telefono = null;
        String rol = null;
        boolean activo = false;
        String materia = null;

        estudianteIds = estudiantesToLongList( profesor.getEstudiantes() );
        boletinIds = boletinesToLongList( profesor.getBoletin() );
        tareaIds = tareasToLongList( profesor.getTarea() );
        calificacionIds = calificacionesToLongList( profesor.getCalificaciones() );
        gradoIds = gradosToLongList( profesor.getGrado() );
        institucion = profesorInstitucionNombre( profesor );
        id = profesor.getId();
        email = profesor.getEmail();
        nombre = profesor.getNombre();
        if ( profesor.getTipoDocumento() != null ) {
            tipoDocumento = profesor.getTipoDocumento().name();
        }
        dni = profesor.getDni();
        apellido = profesor.getApellido();
        telefono = profesor.getTelefono();
        if ( profesor.getRol() != null ) {
            rol = profesor.getRol().name();
        }
        activo = profesor.isActivo();
        if ( profesor.getMateria() != null ) {
            materia = profesor.getMateria().name();
        }

        ProfesorResponseDTO profesorResponseDTO = new ProfesorResponseDTO( id, email, nombre, tipoDocumento, dni, apellido, telefono, institucion, rol, activo, materia, estudianteIds, boletinIds, tareaIds, calificacionIds, gradoIds );

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
        profesor.setTarea( longListToTareas( profesorRequestDTO.tareaIds() ) );
        profesor.setCalificaciones( longListToCalificaciones( profesorRequestDTO.calificacionIds() ) );
        profesor.setGrado( longListToGrados( profesorRequestDTO.gradoIds() ) );
        profesor.setInstitucion( longToInstitucion( profesorRequestDTO.institucionId() ) );
        profesor.setNombre( profesorRequestDTO.nombre() );
        profesor.setApellido( profesorRequestDTO.apellido() );
        profesor.setDni( profesorRequestDTO.dni() );
        if ( profesorRequestDTO.tipoDocumento() != null ) {
            profesor.setTipoDocumento( Enum.valueOf( TipoDocumentoEnum.class, profesorRequestDTO.tipoDocumento() ) );
        }
        profesor.setEmail( profesorRequestDTO.email() );
        profesor.setTelefono( profesorRequestDTO.telefono() );
        profesor.setContrasena( profesorRequestDTO.contrasena() );
        if ( profesorRequestDTO.materia() != null ) {
            profesor.setMateria( Enum.valueOf( MateriaEnum.class, profesorRequestDTO.materia() ) );
        }

        return profesor;
    }

    private String profesorInstitucionNombre(Profesor profesor) {
        if ( profesor == null ) {
            return null;
        }
        Institucion institucion = profesor.getInstitucion();
        if ( institucion == null ) {
            return null;
        }
        String nombre = institucion.getNombre();
        if ( nombre == null ) {
            return null;
        }
        return nombre;
    }
}
