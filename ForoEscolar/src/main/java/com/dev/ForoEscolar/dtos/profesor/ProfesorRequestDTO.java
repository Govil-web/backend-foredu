package com.dev.ForoEscolar.dtos.profesor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;


public record ProfesorRequestDTO (
        Long id,
        @NotNull(message = "El email no puede estar vacio")
        @Email(message = "Debe ingresar una dirección de correo electrónico válida")
        String email,
        @NotNull(message = "Debe ingresar el nombre del usuario")
        String nombre,
        @NotNull(message = "Debe ingresar el apellido del usuario")
        String apellido,
        @NotNull(message = "Debe ingresar el DNI del usuario")
        String dni,
        @NotNull(message = "Debe ingresar el tipo de documento")
        String tipoDocumento,
        @NotNull(message = "Debe agregar un número de teléfono")
        String telefono,
        @NotNull(message = "La contraseña no puede estar vacía")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String contrasena,
        @NotNull(message = "Debe ingresar el nombre de la institucion educativa")
        String institucion,

        @NotNull(message = "La materia no puede estar vacía")
        String materia,
        List<Long> estudianteIds,
        List<Long> boletinIds,
        List<Long> asistenciaIds,
        List<Long> tareaIds,
        List<Long> calificacionIds,
        List<Long> gradoIds

){

}
