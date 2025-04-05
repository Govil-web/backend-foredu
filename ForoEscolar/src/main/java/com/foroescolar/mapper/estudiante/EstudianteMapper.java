package com.foroescolar.mapper.estudiante;

import com.foroescolar.dtos.estudiante.*;
import com.foroescolar.model.Estudiante;
import com.foroescolar.repository.GradoRepository;
import com.foroescolar.repository.TutorLegalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación concreta del mapper que extiende la clase base
 * y agrega capacidades relacionadas con los repositorios
 */
@Component
public class EstudianteMapper extends EstudianteMapperBase {

    private final GradoRepository gradoRepository;
    private final TutorLegalRepository tutorLegalRepository;

    @Autowired
    public EstudianteMapper(GradoRepository gradoRepository, TutorLegalRepository tutorLegalRepository) {
        this.gradoRepository = gradoRepository;
        this.tutorLegalRepository = tutorLegalRepository;
    }

    /**
     * Implementación que requiere acceso a repositorios para configurar relaciones
     */
    @Override
    @Transactional
    public Estudiante crearDesdeDTO(EstudianteCreacionDTO dto) {
        if (dto == null) {
            return null;
        }

        Estudiante estudiante = new Estudiante();
        estudiante.setNombre(dto.nombre());
        estudiante.setApellido(dto.apellido());
        estudiante.setDni(dto.numeroDocumento());
        estudiante.setGenero(dto.genero());
        estudiante.setFechaNacimiento(dto.fechaNacimiento());
        estudiante.setTipoDocumento(dto.tipoDocumento());
        estudiante.setActivo(true);

        // Configurar relaciones
        if (dto.gradoId() != null) {
            estudiante.setGrado(gradoRepository.findById(dto.gradoId()).orElse(null));
        }

        if (dto.tutorLegalId() != null) {
            estudiante.setTutor(tutorLegalRepository.findById(dto.tutorLegalId()).orElse(null));
        }

        return estudiante;
    }

    /**
     * Implementación que requiere acceso a repositorios para configurar relaciones
     */
    @Override
    @Transactional
    public void actualizarDesdeDTO(Estudiante estudiante, EstudianteActualizacionDTO dto) {
        if (estudiante == null || dto == null) {
            return;
        }

        // Utiliza el método de la clase base para actualizar campos básicos
        actualizarCamposBasicos(estudiante, dto);

        // Actualizar relaciones
        if (dto.gradoId() != null) {
            estudiante.setGrado(gradoRepository.findById(dto.gradoId()).orElse(null));
        }

        if (dto.tutorLegalId() != null) {
            estudiante.setTutor(tutorLegalRepository.findById(dto.tutorLegalId()).orElse(null));
        }
    }
}