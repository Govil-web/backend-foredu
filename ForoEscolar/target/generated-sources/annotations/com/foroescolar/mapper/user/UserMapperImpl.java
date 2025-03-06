package com.foroescolar.mapper.user;

import com.foroescolar.dtos.user.UserRequestDTO;
import com.foroescolar.dtos.user.UserResponseDTO;
import com.foroescolar.enums.TipoDocumentoEnum;
import com.foroescolar.model.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-06T05:24:54+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.41.0.z20250213-2037, environment: Java 21.0.5 (Microsoft)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponseDTO toResponseDTO(User user) {
        if ( user == null ) {
            return null;
        }

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

        id = user.getId();
        email = user.getEmail();
        nombre = user.getNombre();
        if ( user.getTipoDocumento() != null ) {
            tipoDocumento = user.getTipoDocumento().name();
        }
        dni = user.getDni();
        apellido = user.getApellido();
        telefono = user.getTelefono();
        institucion = user.getInstitucion();
        if ( user.getRol() != null ) {
            rol = user.getRol().name();
        }
        activo = user.isActivo();

        UserResponseDTO userResponseDTO = new UserResponseDTO( id, email, nombre, tipoDocumento, dni, apellido, telefono, institucion, rol, activo );

        return userResponseDTO;
    }

    @Override
    public User toEntity(UserRequestDTO userRequestDTO) {
        if ( userRequestDTO == null ) {
            return null;
        }

        User user = new User();

        user.setApellido( userRequestDTO.apellido() );
        user.setContrasena( userRequestDTO.contrasena() );
        user.setDni( userRequestDTO.dni() );
        user.setEmail( userRequestDTO.email() );
        user.setInstitucion( userRequestDTO.institucion() );
        user.setNombre( userRequestDTO.nombre() );
        user.setTelefono( userRequestDTO.telefono() );
        if ( userRequestDTO.tipoDocumento() != null ) {
            user.setTipoDocumento( Enum.valueOf( TipoDocumentoEnum.class, userRequestDTO.tipoDocumento() ) );
        }

        return user;
    }
}
