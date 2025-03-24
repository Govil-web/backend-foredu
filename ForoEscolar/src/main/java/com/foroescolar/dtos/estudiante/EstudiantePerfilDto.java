package com.foroescolar.dtos.estudiante;

import com.foroescolar.enums.GeneroEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;
import java.time.LocalDate;

public record EstudiantePerfilDto(

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
    Long grado
    ) implements Serializable {

    }

