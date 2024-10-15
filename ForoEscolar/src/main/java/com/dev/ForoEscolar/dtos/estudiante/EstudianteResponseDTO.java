package com.dev.ForoEscolar.dtos.estudiante;

import com.dev.ForoEscolar.enums.GeneroEnum;
import com.dev.ForoEscolar.enums.TipoDocumentoEnum;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public record EstudianteResponseDTO(


        Long id,
        @NotNull(message = "Debe ingresar el nombre del usuario")
        @NotEmpty(message = "Debe ingresar el nombre del usuario")
        @NotBlank(message = "El nombre no debe poseer caracteres en blanco")
        String nombre,
        @NotNull(message = "Debe ingresar el apellido del usuario")
        @NotEmpty(message = "Debe ingresar el apellido del usuario")
        @NotBlank(message = "El apellido no debe poseer caracteres en blanco")
        String apellido,
        @NotNull(message = "Debe ingresar el DNI del usuario")
        @NotBlank(message = "El DNI no debe poseer caracteres en Blanco")
        String dni,
        GeneroEnum genero,
        @NotNull(message = "La fecha de nacimiento no puede estar vacia")
        LocalDate fechaNacimiento,
        @NotNull(message = "Debe ingresar el tipo de documento")
        @NotEmpty(message = "Debe ingresar el tipo de documento")
        @NotBlank(message = "El tipo de documento no debe poseer caracteres en blanco")
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
