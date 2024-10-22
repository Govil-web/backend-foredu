package com.dev.ForoEscolar.repository;



import com.dev.ForoEscolar.model.Asistencia;
import com.dev.ForoEscolar.model.Estudiante;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstudianteRepository extends GenericRepository<Estudiante, Long>{


    List<Estudiante> findByGradoId(Long gradoId);

    @Query("SELECT a FROM Asistencia a WHERE a.estudiante.id = :id")
    List<Asistencia> findByEstudianteId(Long id);


}
