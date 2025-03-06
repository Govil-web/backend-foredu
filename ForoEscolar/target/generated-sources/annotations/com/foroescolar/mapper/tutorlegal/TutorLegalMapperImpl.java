package com.foroescolar.mapper.tutorlegal;

import com.foroescolar.dtos.tutorlegal.TutorLegalRequestDTO;
import com.foroescolar.dtos.tutorlegal.TutorLegalResponseDTO;
import com.foroescolar.enums.TipoDocumentoEnum;
import com.foroescolar.model.TutorLegal;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-06T05:24:54+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.41.0.z20250213-2037, environment: Java 21.0.5 (Microsoft)"
)
@Component
public class TutorLegalMapperImpl extends TutorLegalMapper {

    @Override
    public TutorLegal toEntity(TutorLegalRequestDTO tutorLegalRequestDTO) {
        if ( tutorLegalRequestDTO == null ) {
            return null;
        }

        TutorLegal tutorLegal = new TutorLegal();

        tutorLegal.setEstudiante( longListToEstudiante( tutorLegalRequestDTO.estudiante() ) );
        tutorLegal.setApellido( tutorLegalRequestDTO.apellido() );
        tutorLegal.setContrasena( tutorLegalRequestDTO.contrasena() );
        tutorLegal.setDni( tutorLegalRequestDTO.dni() );
        tutorLegal.setEmail( tutorLegalRequestDTO.email() );
        tutorLegal.setId( tutorLegalRequestDTO.id() );
        tutorLegal.setInstitucion( tutorLegalRequestDTO.institucion() );
        tutorLegal.setNombre( tutorLegalRequestDTO.nombre() );
        tutorLegal.setTelefono( tutorLegalRequestDTO.telefono() );
        if ( tutorLegalRequestDTO.tipoDocumento() != null ) {
            tutorLegal.setTipoDocumento( Enum.valueOf( TipoDocumentoEnum.class, tutorLegalRequestDTO.tipoDocumento() ) );
        }

        return tutorLegal;
    }

    @Override
    public TutorLegalResponseDTO toResponseDTO(TutorLegal tutorLegal) {
        if ( tutorLegal == null ) {
            return null;
        }

        List<Long> estudiante = null;
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

        estudiante = estudianteToLongList( tutorLegal.getEstudiante() );
        id = tutorLegal.getId();
        email = tutorLegal.getEmail();
        nombre = tutorLegal.getNombre();
        if ( tutorLegal.getTipoDocumento() != null ) {
            tipoDocumento = tutorLegal.getTipoDocumento().name();
        }
        dni = tutorLegal.getDni();
        apellido = tutorLegal.getApellido();
        telefono = tutorLegal.getTelefono();
        institucion = tutorLegal.getInstitucion();
        if ( tutorLegal.getRol() != null ) {
            rol = tutorLegal.getRol().name();
        }
        activo = tutorLegal.isActivo();

        TutorLegalResponseDTO tutorLegalResponseDTO = new TutorLegalResponseDTO( id, email, nombre, tipoDocumento, dni, apellido, telefono, institucion, rol, activo, estudiante );

        return tutorLegalResponseDTO;
    }
}
