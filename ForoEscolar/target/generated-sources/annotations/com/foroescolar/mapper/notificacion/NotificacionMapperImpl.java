package com.foroescolar.mapper.notificacion;

import com.foroescolar.dtos.notificacion.NotificacionDTO;
import com.foroescolar.enums.TipoNotificacionEnum;
import com.foroescolar.model.Notificacion;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-06T05:24:54+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.41.0.z20250213-2037, environment: Java 21.0.5 (Microsoft)"
)
@Component
public class NotificacionMapperImpl extends NotificacionMapper {

    @Override
    public NotificacionDTO toResponseDto(Notificacion notificacion) {
        if ( notificacion == null ) {
            return null;
        }

        Long user = null;
        Long tutorLegal = null;
        Long id = null;
        String titulo = null;
        LocalDate fecha = null;
        TipoNotificacionEnum tipo = null;
        String mensaje = null;

        user = userToLong( notificacion.getUser() );
        tutorLegal = tutorLegalToLong( notificacion.getTutorLegal() );
        id = notificacion.getId();
        titulo = notificacion.getTitulo();
        fecha = notificacion.getFecha();
        tipo = notificacion.getTipo();
        mensaje = notificacion.getMensaje();

        NotificacionDTO notificacionDTO = new NotificacionDTO( id, titulo, fecha, tipo, mensaje, user, tutorLegal );

        return notificacionDTO;
    }

    @Override
    public Notificacion toEntity(NotificacionDTO notificacionDTO) {
        if ( notificacionDTO == null ) {
            return null;
        }

        Notificacion notificacion = new Notificacion();

        notificacion.setUser( longToUser( notificacionDTO.user() ) );
        notificacion.setTutorLegal( longToTutorLegal( notificacionDTO.tutorLegal() ) );
        notificacion.setFecha( notificacionDTO.fecha() );
        notificacion.setId( notificacionDTO.id() );
        notificacion.setMensaje( notificacionDTO.mensaje() );
        notificacion.setTipo( notificacionDTO.tipo() );
        notificacion.setTitulo( notificacionDTO.titulo() );

        return notificacion;
    }
}
