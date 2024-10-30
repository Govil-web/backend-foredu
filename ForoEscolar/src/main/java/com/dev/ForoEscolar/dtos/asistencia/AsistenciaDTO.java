package com.dev.ForoEscolar.dtos.asistencia;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;


@Builder
@Data
public class AsistenciaDTO implements Serializable {

    Long id;
    boolean asistio;
    boolean contadorClases;
    int contadorTotal;
    int asistenciaAlumno;
    LocalDate fecha;
    String observaciones;
    double porcentajeAsistencia;
    Long profesor;
    Long estudiante;
    Long grado;

}

