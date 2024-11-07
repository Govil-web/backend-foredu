package com.dev.ForoEscolar.dtos.estudiante;

import com.dev.ForoEscolar.enums.GeneroEnum;
import com.dev.ForoEscolar.enums.TipoDocumentoEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public record EstudianteResponseDTO(


        Long id,

        @NotBlank(message = " Debe ingresar el nombre del usuario")
        String nombre,

        @NotBlank(message = "Debe ingresar el apellido del usuario")
        String apellido,

        @NotBlank(message = "Debe ingresar el DNI del usuario")
        @Pattern(regexp = "\\d+", message = "El DNI solo debe contener números")
        String dni,
        GeneroEnum genero,
        @NotNull(message = "Debe ingresar la fecha de nacimiento")
        LocalDate fechaNacimiento,

        @Enumerated(EnumType.STRING)
        @NotBlank(message = "Debe ingresar el tipo de documento")
       String tipoDocumento,
        Boolean activo,
        Long tutor,
        Long grado,
        List<Long> asistencia,
        List<Long> boletin,
        List<Long> tarea,
        List<Long> calificaciones
) implements Serializable {

}
