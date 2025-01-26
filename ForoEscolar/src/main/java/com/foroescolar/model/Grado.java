package com.foroescolar.model;

import com.foroescolar.enums.AulaEnum;
import com.foroescolar.enums.CursoEnum;
import com.foroescolar.enums.MateriaEnum;
import com.foroescolar.enums.TurnoEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Grado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private AulaEnum aula;
    @Enumerated(value = EnumType.STRING)
    private CursoEnum curso;
    @Enumerated(value = EnumType.STRING)
    private TurnoEnum turno;
    @Enumerated(value = EnumType.STRING)
    private MateriaEnum materia;

    @ManyToOne
    @JoinColumn(name="profesor_id")
    private Profesor profesor;

    @OneToMany(mappedBy = "grado",cascade = {CascadeType.ALL},orphanRemoval = true,fetch = FetchType.LAZY)
    private List<Estudiante> estudiantes;

    private int contador= 0;

    public void incrementarContador(){
        this.contador++;
    }


}
