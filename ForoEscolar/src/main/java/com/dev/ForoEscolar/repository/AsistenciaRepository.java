package com.dev.ForoEscolar.repository;

import com.dev.ForoEscolar.model.Asistencia;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsistenciaRepository extends GenericRepository<Asistencia, Long> {

    long countByAsistio(boolean asistio);

    long count();

    int countByContadorClases(boolean contadorClases);

  //  long countByEstudianteIdAndContadorClasesTrue(Long estudianteId);

    int countByEstudianteIdAndAsistioTrue(Long estudianteId);


}
