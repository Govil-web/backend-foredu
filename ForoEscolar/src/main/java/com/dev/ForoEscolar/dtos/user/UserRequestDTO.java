package com.dev.ForoEscolar.dtos.user;

import jakarta.validation.constraints.*;

public record UserRequestDTO(

        @NotNull(message = "El email no puede estar vacio")
        @Email(message = "Debe ingresar una dirección de correo electrónico válida")
        String email,

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
        @NotNull(message = "Debe ingresar el tipo de documento")
        @NotEmpty(message = "Debe ingresar el tipo de documento")
        @NotBlank(message = "El tipo de documento no debe poseer caracteres en blanco")
        String tipoDocumento,
        @NotNull(message = "Debe agregar un número de teléfono")
        String telefono,
        @NotNull(message = "La contraseña no puede estar vacía")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String contrasena,
        @NotNull(message = "Debe ingresar el nombre de la institucion educativa")
        String institucion

) {
}
