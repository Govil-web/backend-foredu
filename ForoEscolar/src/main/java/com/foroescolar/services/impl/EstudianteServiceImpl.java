package com.foroescolar.services.impl;

import com.foroescolar.dtos.asistencia.AsistenciaDTO;
import com.foroescolar.dtos.estudiante.*;
import com.foroescolar.enums.RoleEnum;
import com.foroescolar.exceptions.model.DniDuplicadoException;
import com.foroescolar.exceptions.model.EntityNotFoundException;
import com.foroescolar.mapper.asistencia.AsistenciaMapper;
import com.foroescolar.mapper.estudiante.EstudianteMapper;
import com.foroescolar.model.Asistencia;
import com.foroescolar.model.Estudiante;
import com.foroescolar.repository.EstudianteRepository;
import com.foroescolar.services.EstudianteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EstudianteServiceImpl implements EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final EstudianteMapper estudianteMapper;
    private final AsistenciaMapper asistenciaMapper;

    @Autowired
    public EstudianteServiceImpl(
            EstudianteRepository estudianteRepository,
            EstudianteMapper estudianteMapper,
            AsistenciaMapper asistenciaMapper) {
        this.estudianteRepository = estudianteRepository;
        this.estudianteMapper = estudianteMapper;
        this.asistenciaMapper = asistenciaMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstudianteListaDTO> obtenerTodos() {
        return estudianteRepository.findAll()
                .stream()
                .map(estudianteMapper::mapearAListaDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EstudianteListaDTO> obtenerTodosPaginados(int pagina, int tamano) {
        Pageable pageable = PageRequest.of(pagina, tamano);
        return estudianteRepository.findAll(pageable)
                .map(estudianteMapper::mapearAListaDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EstudianteDetalleDTO> obtenerDetallePorId(Long id) {
        return estudianteRepository.findById(id)
                .map(estudianteMapper::mapearADetalleDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstudianteListaDTO> obtenerPorGrado(Long gradoId) {
        return estudianteRepository.findByGradoId(gradoId)
                .stream()
                .map(estudianteMapper::mapearAListaDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EstudianteDetalleDTO crear(EstudianteCreacionDTO dto) {
        // Validar campos únicos
        if (estudianteRepository.existsByDni(dto.numeroDocumento())) {
            throw new DniDuplicadoException(dto.numeroDocumento());
        }

        Estudiante estudiante = estudianteMapper.crearDesdeDTO(dto);
        estudiante.setRol(RoleEnum.ROLE_ESTUDIANTE);
        estudiante.setActivo(true);

        estudiante = estudianteRepository.save(estudiante);
        return estudianteMapper.mapearADetalleDTO(estudiante);
    }

    @Override
    @Transactional
    public EstudianteDetalleDTO actualizar(EstudianteActualizacionDTO dto) {
        Estudiante estudiante = estudianteRepository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado"));

        // Verificar si se está cambiando el DNI y si ya existe
        if (dto.numeroDocumento() != null &&
                !dto.numeroDocumento().equals(estudiante.getDni()) &&
                estudianteRepository.existsByDni(dto.numeroDocumento())) {
            throw new DniDuplicadoException(dto.numeroDocumento());
        }

        estudianteMapper.actualizarDesdeDTO(estudiante, dto);
        estudiante = estudianteRepository.save(estudiante);

        return estudianteMapper.mapearADetalleDTO(estudiante);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado"));
        estudianteRepository.delete(estudiante);
    }

    @Override
    @Transactional
    public boolean cambiarEstadoActivo(Long id) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado"));

        boolean nuevoEstado = !estudiante.getActivo();
        estudiante.setActivo(nuevoEstado);
        estudianteRepository.save(estudiante);

        return nuevoEstado;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsistenciaDTO> obtenerAsistencias(Long estudianteId) {
        List<Asistencia> asistencias = estudianteRepository.findByEstudianteId(estudianteId);
        return asistencias.stream()
                .map(asistenciaMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Estudiante obtenerEntidadPorId(Long id) {
        return estudianteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Estudiante> obtenerEntidadesPorIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return estudianteRepository.findAllById(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstudianteResumenDTO> obtenerTodosResumen() {
        return estudianteRepository.findAllResumen();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EstudianteResumenDTO> obtenerTodosResumenPaginados(int pagina, int tamano) {
        Pageable pageable = PageRequest.of(pagina, tamano);
        return estudianteRepository.findAllResumen(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstudianteResumenDTO> obtenerResumenPorTutor(Long tutorId) {
        return estudianteRepository.findAllByTutorId(tutorId);
    }

    @Override
    public Estudiante findByIdToEntity(Long estudianteId) {
        return estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado"));
    }
}