package com.foroescolar.services.impl;

import com.foroescolar.dtos.asistencia.AsistenciaDTO;
import com.foroescolar.dtos.estudiante.EstudianteResponseDTO;
import com.foroescolar.enums.RoleEnum;
import com.foroescolar.exceptions.model.DniDuplicadoException;
import com.foroescolar.exceptions.model.EntityNotFoundException;
import com.foroescolar.mapper.asistencia.AsistenciaMapper;
import com.foroescolar.mapper.estudiante.EstudianteMapper;
import com.foroescolar.model.Asistencia;
import com.foroescolar.model.Estudiante;
import com.foroescolar.repository.EstudianteRepository;
import com.foroescolar.services.EstudianteService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EstudianteServiceImpl implements EstudianteService {


    private final EstudianteRepository estudianteRepository;
    private final EstudianteMapper estudianteMapper;
    private final AsistenciaMapper asistenciaMapper;

    @Autowired
    public EstudianteServiceImpl(EstudianteRepository estudianteRepository, EstudianteMapper estudianteMapper, AsistenciaMapper asistenciaMapper) {
        this.estudianteRepository = estudianteRepository;
        this.estudianteMapper = estudianteMapper;
        this.asistenciaMapper = asistenciaMapper;
    }

    @Transactional
    @Override
    public EstudianteResponseDTO save(EstudianteResponseDTO estudianteRequestDTO) {

        validateUniqueFields(estudianteRequestDTO);

        Estudiante estudiante = estudianteMapper.toEntity(estudianteRequestDTO);
        estudiante.setRol(RoleEnum.valueOf("ROLE_ESTUDIANTE"));
        estudiante.setActivo(true);
        estudiante = estudianteRepository.save(estudiante);
        return estudianteMapper.toResponseDTO(estudiante);


    }

    @Override
    public Optional<EstudianteResponseDTO> findById(Long id) {
        Optional<Estudiante> estudiante = estudianteRepository.findById(id);
        return estudiante.map(estudianteMapper::toResponseDTO);
    }

    @Override
    public List<EstudianteResponseDTO> findAll() {
        List<Estudiante> estudiantes = estudianteRepository.findAll();
        return estudiantes.stream()
                .map(estudianteMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Estudiante estudiante = estudianteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado"));
        estudianteRepository.delete(estudiante);
    }


    @Transactional
    @Override
    public EstudianteResponseDTO update(EstudianteResponseDTO estudianteRequestDTO) {
        // Buscamos el estudiante por ID
        Estudiante estudianteExistente = estudianteRepository.findById(estudianteRequestDTO.id())
                .orElseThrow(() -> new EntityNotFoundException("La entidad con ese ID " + estudianteRequestDTO.id() + " no fue encontrada"));

        // Mapeamos el DTO recibido a una entidad Estudiante
        Estudiante estudianteRequest = estudianteMapper.toEntity(estudianteRequestDTO);

        // Actualizamos solo los campos que son modificados (como el nombre)
        actualizarCampos(estudianteExistente, estudianteRequest);

        // Guardamos la entidad actualizada y la convertimos en DTO de respuesta
        Estudiante estudianteActualizado = estudianteRepository.save(estudianteExistente);
        return estudianteMapper.toResponseDTO(estudianteActualizado);
    }

    /**
     * Método que actualiza solo los campos no coleccionados del estudiante.
     */
    private void actualizarCampos(Estudiante estudianteExistente, Estudiante estudianteRequest) {
        // Actualizamos solo los campos que necesitan ser modificados, como el nombre
        if (estudianteRequest.getNombre() != null) {
            estudianteExistente.setNombre(estudianteRequest.getNombre());
        }
        if (estudianteRequest.getApellido() != null) {
            estudianteExistente.setApellido(estudianteRequest.getApellido());
        }
        if (estudianteRequest.getDni() != null) {
            estudianteExistente.setDni(estudianteRequest.getDni());
        }
        if (estudianteRequest.getGenero() != null) {
            estudianteExistente.setGenero(estudianteRequest.getGenero());
        }
        if (estudianteRequest.getFechaNacimiento() != null) {
            estudianteExistente.setFechaNacimiento(estudianteRequest.getFechaNacimiento());
        }
        if (estudianteRequest.getActivo() != null) {
            estudianteExistente.setActivo(estudianteRequest.getActivo());
        }
        if (estudianteRequest.getTipoDocumento() != null) {
            estudianteExistente.setTipoDocumento(estudianteRequest.getTipoDocumento());
        }


    }


    @Override
    public List<EstudianteResponseDTO> findByGradoId(Long gradoId) {
        List<Estudiante> estudiantes = estudianteRepository.findByGradoId(gradoId);
        return estudiantes.stream().map(estudianteMapper::toResponseDTO).toList();
    }


    @Override
    public List<AsistenciaDTO> findByEstudianteId(Long id) {
        List<Asistencia> asistencias = estudianteRepository.findByEstudianteId(id);
        return asistencias.stream()
                .map(asistenciaMapper::toResponseDto)
                .toList();
    }


    /**
     * Método que permite suscribir o unsubscribe a un estudiante
     *
     * @param id id del estudiante
     * @return true si se suscribe, false si se unsubscribe
     */
    @Override
    public boolean subscribe_unsubscribe(Long id) {
        Estudiante estudiante = estudianteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado"));
        if (Boolean.TRUE.equals(estudiante.getActivo())) {
            estudiante.setActivo(false);
            estudianteRepository.save(estudiante);
            return false;
        }
        estudiante.setActivo(true);
        estudianteRepository.save(estudiante);

        return true;
    }

    private void validateUniqueFields(EstudianteResponseDTO dto) {
        // Validar DNI
        if (estudianteRepository.existsByDni(dto.dni())) {
            throw new DniDuplicadoException(dto.dni());

        }

        // Otras validaciones específicas del negocio...
    }

    @Override
    public List<Estudiante> findByIds(List<Long> id) {

        List<Estudiante> estudiantesList = new ArrayList<>();
        for (Long idEstudiante : id) {
            Optional<Estudiante> estudiante = estudianteRepository.findById(idEstudiante);
            estudiante.ifPresent(estudiantesList::add);
        }
        return estudiantesList;
    }
    @Override
    public Estudiante findByIdToEntity(Long id){
        return estudianteRepository.findById(id).orElse(null);
    }

}
