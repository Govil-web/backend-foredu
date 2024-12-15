package com.dev.ForoEscolar.services.impl;

import com.dev.ForoEscolar.dtos.asistencia.AsistenciaDTO;
import com.dev.ForoEscolar.dtos.estudiante.EstudianteResponseDTO;
import com.dev.ForoEscolar.enums.RoleEnum;
import com.dev.ForoEscolar.exceptions.ApplicationException;
import com.dev.ForoEscolar.mapper.asistencia.AsistenciaMapper;
import com.dev.ForoEscolar.mapper.estudiante.EstudianteMapper;
import com.dev.ForoEscolar.model.Asistencia;
import com.dev.ForoEscolar.model.Estudiante;
import com.dev.ForoEscolar.model.UpdatedEntities;
import com.dev.ForoEscolar.repository.EstudianteRepository;
import com.dev.ForoEscolar.services.EstudianteService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Estudiante estudiante = estudianteRepository.findById(id).orElseThrow(() -> new ApplicationException("Estudiante no encontrado"));
        estudianteRepository.delete(estudiante);
    }

    @Transactional
    @Override
    public EstudianteResponseDTO update(EstudianteResponseDTO estudianteRequestDTO) {
        // Buscamos el estudiante por ID
        Estudiante estudianteExistente = estudianteRepository.findById(estudianteRequestDTO.id())
                .orElseThrow(() -> new ApplicationException("La entidad con ese ID " + estudianteRequestDTO.id() + " no fue encontrada"));

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

//    @Transactional
//    @Override
//    public EstudianteResponseDTO update(EstudianteResponseDTO estudianteRequestDTO) {
//     Optional<Estudiante> estudiante= estudianteRepository.findById(estudianteRequestDTO.id());
//     if(estudiante.isPresent()){
//        Estudiante estudianteRequest=estudianteMapper.toEntity(estudianteRequestDTO);
//         Estudiante updateEstudiante= UpdatedEntities.update(estudiante.get(),estudianteRequest);
//         return estudianteMapper.toResponseDTO(estudianteRepository.save(updateEstudiante));
//        } else {
//            throw new ApplicationException("La entidad con ese ID "+ estudianteRequestDTO.id() +"  no fue encontrado");
//        }
//    }


    @Override
    public List<EstudianteResponseDTO> findByGradoId(Long gradoId) {
        List<Estudiante> estudiantes= estudianteRepository.findByGradoId(gradoId);
        return estudiantes.stream().map(estudianteMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<AsistenciaDTO> findByEstudianteId(Long id) {
       List<Asistencia> asistencias= estudianteRepository.findByEstudianteId(id);
        return asistencias.stream()
                .map(asistenciaMapper::toResponseDto)
                .collect(Collectors.toList());
    }



}
