package com.foroescolar.dtos.asistencia;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class DetalleAsistenciaByAlumno {

    private Long idEstudiante;
    private String nombreEstudiante;
    private String grado;
    private Integer AsistenciasPresente;
    private Integer AsistenciasAusente;
    private Integer AsistenciasJustificadas;
    private Integer AsistenciasTarde;
    private Integer clasesVistasDelGrado;
    private Double porcentajeDeAsistencias;
}
