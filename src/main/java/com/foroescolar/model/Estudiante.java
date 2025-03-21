package com.foroescolar.model;

import com.foroescolar.enums.GeneroEnum;
import com.foroescolar.enums.RoleEnum;
import com.foroescolar.enums.TipoDocumentoEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "estudiantes")
public class Estudiante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String apellido;

    private String dni;

    @Enumerated(EnumType.STRING)
    private GeneroEnum genero;

    private Boolean activo;

    @Enumerated(EnumType.STRING)
    private RoleEnum rol;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;


    @ManyToOne
    @JoinColumn(name = "grado_id", nullable = false, foreignKey = @ForeignKey(name="FK_GRADO"))
    private Grado grado;

    @Column(name = "tipo_documento", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoDocumentoEnum tipoDocumento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_legal_id",  referencedColumnName = "user_id")
    private TutorLegal tutor;

    @ManyToMany(mappedBy = "estudiantes",cascade ={CascadeType.MERGE,CascadeType.REFRESH,CascadeType.PERSIST}, fetch = FetchType.LAZY)
    private List<Profesor> profesores;

    @OneToMany(mappedBy = "estudiante",cascade = {CascadeType.ALL},orphanRemoval = true,fetch = FetchType.LAZY)
    private List<Boletin> boletin  = new ArrayList<>();

    @OneToMany(mappedBy = "estudiante",cascade = {CascadeType.ALL},orphanRemoval = true,fetch = FetchType.LAZY)
    private List<Asistencia> asistencia  = new ArrayList<>();

    @OneToMany(mappedBy = "estudiante",cascade = {CascadeType.ALL},orphanRemoval = true,fetch = FetchType.LAZY)
    private List<Tarea> tarea  = new ArrayList<>();

    @OneToMany(mappedBy = "estudiante", cascade = {CascadeType.ALL}, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Calificacion> calificaciones  = new ArrayList<>();

    //Necesario para desvincular la tabla Estudiantes de Profesor y evitar borrar todas las tablas relacionales
    @PreRemove
    public void preRemove() {
        //Se Remueve de Estudiante el Profesor, y se despega de la tabla profesor_estudiante antes de ejecutar el DELETE
        if (this.profesores != null) {
            this.profesores.forEach(profesor -> profesor.getEstudiantes().remove(this));
        }
    }
}
