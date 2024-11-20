package com.dev.ForoEscolar.repository;

import com.dev.ForoEscolar.model.Grado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GradoRepository extends JpaRepository<Grado,Long> {


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
}
