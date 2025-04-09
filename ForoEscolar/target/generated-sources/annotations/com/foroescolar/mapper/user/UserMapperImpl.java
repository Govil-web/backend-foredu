package com.foroescolar.mapper.user;

import com.foroescolar.dtos.user.UserRequestDTO;
import com.foroescolar.dtos.user.UserResponseDTO;
import com.foroescolar.enums.TipoDocumentoEnum;
import com.foroescolar.model.Institucion;
import com.foroescolar.model.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-08T21:13:46-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 22 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponseDTO toResponseDTO(User user) {
        if ( user == null ) {
            return null;
        }

        Long institucionId = null;
        Long id = null;
        String email = null;
        String nombre = null;
        String tipoDocumento = null;
        String dni = null;
        String apellido = null;
        String telefono = null;
        String rol = null;
        boolean activo = false;

        institucionId = userInstitucionId( user );
        id = user.getId();
        email = user.getEmail();
        nombre = user.getNombre();
        if ( user.getTipoDocumento() != null ) {
            tipoDocumento = user.getTipoDocumento().name();
        }
        dni = user.getDni();
        apellido = user.getApellido();
        telefono = user.getTelefono();
        if ( user.getRol() != null ) {
            rol = user.getRol().name();
        }
        activo = user.isActivo();

        UserResponseDTO userResponseDTO = new UserResponseDTO( id, email, nombre, tipoDocumento, dni, apellido, telefono, institucionId, rol, activo );

        return userResponseDTO;
    }

    @Override
    public User toEntity(UserRequestDTO userRequestDTO) {
        if ( userRequestDTO == null ) {
            return null;
        }

        User user = new User();

        user.setNombre( userRequestDTO.nombre() );
        user.setApellido( userRequestDTO.apellido() );
        user.setDni( userRequestDTO.dni() );
        if ( userRequestDTO.tipoDocumento() != null ) {
            user.setTipoDocumento( Enum.valueOf( TipoDocumentoEnum.class, userRequestDTO.tipoDocumento() ) );
        }
        user.setEmail( userRequestDTO.email() );
        user.setTelefono( userRequestDTO.telefono() );
        user.setContrasena( userRequestDTO.contrasena() );

        return user;
    }

    private Long userInstitucionId(User user) {
        if ( user == null ) {
            return null;
        }
        Institucion institucion = user.getInstitucion();
        if ( institucion == null ) {
            return null;
        }
        Long id = institucion.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
