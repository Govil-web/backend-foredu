package com.foroescolar.services;

import com.foroescolar.dtos.asistencia.AsistenciaDTO;
import com.foroescolar.dtos.estudiante.*;
import com.foroescolar.model.Estudiante;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz de servicio para operaciones relacionadas con estudiantes
 */
public interface EstudianteService {

    // Métodos para obtener estudiantes
    List<EstudianteListaDTO> obtenerTodos();
    Page<EstudianteListaDTO> obtenerTodosPaginados(int pagina, int tamano);
    Optional<EstudianteDetalleDTO> obtenerDetallePorId(Long id);
    List<EstudianteListaDTO> obtenerPorGrado(Long gradoId);

    // Métodos para manipular estudiantes
    EstudianteDetalleDTO crear(EstudianteCreacionDTO estudiante);
    EstudianteDetalleDTO actualizar(EstudianteActualizacionDTO estudiante);
    void eliminar(Long id);

    // Métodos para operaciones específicas
    boolean cambiarEstadoActivo(Long id);
    List<AsistenciaDTO> obtenerAsistencias(Long estudianteId);

    // Métodos para uso interno (pueden no exponer DTOs)
    Estudiante obtenerEntidadPorId(Long id);
    List<Estudiante> obtenerEntidadesPorIds(List<Long> ids);
    // Métodos para trabajar con EstudianteResumenDTO
    List<EstudianteResumenDTO> obtenerTodosResumen();
    Page<EstudianteResumenDTO> obtenerTodosResumenPaginados(int pagina, int tamano);
    List<EstudianteResumenDTO> obtenerResumenPorTutor(Long tutorId);

    Estudiante findByIdToEntity(Long estudianteId);
}