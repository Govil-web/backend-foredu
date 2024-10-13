package com.dev.ForoEscolar.dtos.estudiante;

import com.dev.ForoEscolar.enums.GeneroEnum;
import com.dev.ForoEscolar.enums.TipoDocumentoEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public record EstudianteResponseDTO(


        Long id,
        @NotNull(message = "Debe ingresar el nombre del usuario")
        String nombre,
        @NotNull(message = "Debe ingresar el apellido del usuario")
        String apellido,
        @NotNull(message = "Debe ingresar el DNI del usuario")
        String dni,
        GeneroEnum genero,
        @NotNull(message = "La fecha de nacimiento no puede estar vacia")
        LocalDate fechaNacimiento,
        @NotNull(message = "Debe ingresar el tipo de documento")
       TipoDocumentoEnum tipoDocumento,
        Boolean activo,
        Long tutor,
        Long grado,
        List<Long> asistencia,
        List<Long> boletin,
        List<Long> tarea,
        List<Long> calificaciones
) implements Serializable {

}
