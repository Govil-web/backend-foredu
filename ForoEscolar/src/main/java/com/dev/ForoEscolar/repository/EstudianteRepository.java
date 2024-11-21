package com.dev.ForoEscolar.repository;



import com.dev.ForoEscolar.model.Asistencia;
import com.dev.ForoEscolar.model.Estudiante;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstudianteRepository extends GenericRepository<Estudiante, Long>{


    List<Estudiante> findByGradoId(Long gradoId);

    @Query("SELECT a FROM Asistencia a WHERE a.estudiante.id = :id")
    List<Asistencia> findByEstudianteId(Long id);
//    List<Asistencia> findAsistenciasByEstudianteId(Long id);

    List<Estudiante> findByTutorId(Long idTutor);

    boolean existsByIdAndTutorId(Long requestedUserId, Long id);

    // Verificar si existe un estudiante con el ID y que tenga un profesor específico
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
            "FROM Estudiante e " +
            "JOIN e.profesores p " +
            "WHERE e.id = :estudianteId AND p.id = :profesorId")
    boolean existsByIdAndProfesores(@Param("estudianteId") Long estudianteId,
                                    @Param("profesorId") Long profesorId);
}
