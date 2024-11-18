package com.dev.ForoEscolar.repository;

import com.dev.ForoEscolar.model.Asistencia;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AsistenciaRepository extends GenericRepository<Asistencia, Long> {

    long countByAsistio(boolean asistio);

    long count();

    int countByContadorClases(boolean contadorClases);

    List<Asistencia> findByGradoId(Long gradoId);
    List<Asistencia> findByEstudianteIdAndGradoId(Long estudianteId, Long gradoId);

    List<Asistencia> findByEstudianteId(Long estudianteId);

    int countByEstudianteIdAndAsistioTrue(Long estudianteId);
    List<Asistencia> findByFechaBetweenAndGradoId(LocalDate startDate, LocalDate endDate, Long gradoId);

    boolean existsByFechaAndGradoId(LocalDate fecha, Long gradoId);


}
