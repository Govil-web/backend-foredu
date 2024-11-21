package com.dev.ForoEscolar.repository;

import com.dev.ForoEscolar.enums.AulaEnum;
import com.dev.ForoEscolar.enums.CursoEnum;
import com.dev.ForoEscolar.enums.MateriaEnum;
import com.dev.ForoEscolar.enums.TurnoEnum;
import com.dev.ForoEscolar.model.Grado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GradoRepository extends JpaRepository<Grado,Long> {

    // Buscar grados por profesor
    List<Grado> findByProfesorId(Long profesorId);
    // Buscar grados que tienen estudiantes con un tutor específico
    @Query("SELECT DISTINCT g FROM Grado g " +
            "JOIN g.estudiantes e " +
            "WHERE e.tutor.id = :tutorId")
    List<Grado> findByEstudiantesTutorId(@Param("tutorId") Long tutorId);

    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END " +
            "FROM Grado g " +
            "JOIN g.profesor p " +
            "WHERE g.id = :gradoId AND p.id = :profesorId")
    boolean existsByIdAndProfesorId(@Param("gradoId") Long gradoId,
                                    @Param("profesorId") Long profesorId);

    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END " +
            "FROM Grado g " +
            "JOIN g.estudiantes e " +
            "WHERE g.id = :gradoId AND e.tutor.id = :tutorId")
    boolean existsByIdAndEstudiantesTutorId(@Param("gradoId") Long gradoId,
                                            @Param("tutorId") Long tutorId);

    // Verificar si existe otro grado con la misma aula y turno (excluyendo el ID actual)
    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END FROM Grado g " +
            "WHERE g.aula = :aula AND g.turno = :turno AND g.id != :id")
    boolean existsByAulaAndTurnoAndIdNot(
            @Param("aula") AulaEnum aula,
            @Param("turno") TurnoEnum turno,
            @Param("id") Long id
    );

    // Contar cuántos grados tiene asignados un profesor
    int countByProfesorId(Long profesorId);

    // Verificar si existe un grado con la misma combinación de aula y turno
    boolean existsByAulaAndTurno(AulaEnum aula, TurnoEnum turno);

    // Buscar grados por turno
    List<Grado> findByTurno(TurnoEnum turno);

    // Buscar grados por materia
    List<Grado> findByMateria(MateriaEnum materia);

    // Buscar grados por curso
    List<Grado> findByCurso(CursoEnum curso);


}
