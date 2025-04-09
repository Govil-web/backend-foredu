package com.foroescolar.dtos.institucion;

import com.foroescolar.enums.NivelEducativo;

public record InstitucionResponseDto (
    Long id,
    String nombre,
    String direccion,
    String telefono,
    String email,
    String logo,
    String identificacion,
    NivelEducativo nivelEducativo
    ){ }
