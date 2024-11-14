package com.dev.ForoEscolar.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "asistencia")
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "asistio")
    private boolean asistio= false;

    private boolean contadorClases= false;

    private int contadorTotal=0;

    private int asistenciaAlumno;


    @Column(name = "fecha")
    private LocalDate fecha;

    private String observaciones;

    @ManyToOne
    @JoinColumn(name = "profesor_id")
    private Profesor profesor;

    @ManyToOne
    @JoinColumn(name = "estudiante_id")
    private Estudiante estudiante;

    @ManyToOne
    @JoinColumn(name = "grado_id")
    private Grado grado;

    public void actualizarContadorGrado(){
        if(this.grado!=null){
            this.grado.incrementarContador();
        }
    }

    // Dias que hubo clases y si el alumno asistio
    @PrePersist
    private void diasDeClasesTranscurridos(){
//        if(this.contadorClases){
//            this.contadorTotal ++;
//        }
        if(asistio){
            this.asistenciaAlumno ++;
        }
       this.actualizarContadorGrado();
    }

}