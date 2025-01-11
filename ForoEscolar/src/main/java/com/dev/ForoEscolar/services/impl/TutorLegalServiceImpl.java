package com.dev.ForoEscolar.services.impl;

import com.dev.ForoEscolar.dtos.asistencia.AsistenciaDTO;
import com.dev.ForoEscolar.dtos.tutorlegal.TutorLegalRequestDTO;
import com.dev.ForoEscolar.dtos.tutorlegal.TutorLegalResponseDTO;
import com.dev.ForoEscolar.enums.RoleEnum;
import com.dev.ForoEscolar.exceptions.ApplicationException;
import com.dev.ForoEscolar.mapper.tutorlegal.TutorLegalMapper;
import com.dev.ForoEscolar.model.Estudiante;
import com.dev.ForoEscolar.model.TutorLegal;
import com.dev.ForoEscolar.model.UpdatedEntities;
import com.dev.ForoEscolar.repository.EstudianteRepository;
import com.dev.ForoEscolar.repository.TutorLegalRepository;
import com.dev.ForoEscolar.services.AsistenciaService;
import com.dev.ForoEscolar.services.TutorLegalService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TutorLegalServiceImpl implements TutorLegalService {

    private final TutorLegalRepository tutorLegalRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TutorLegalMapper tutorLegalMapper;
    private final AsistenciaService asistenciaService;
    private final EstudianteRepository estudianteRepository;

    @Autowired
    public TutorLegalServiceImpl(TutorLegalRepository tutorLegalRepository, PasswordEncoder passwordEncoder,
                                 AsistenciaService asistenciaService, TutorLegalMapper tutorLegalMapper,
                                 EstudianteRepository estudianteRepository) {
        this.tutorLegalRepository = tutorLegalRepository;
        this.passwordEncoder = (BCryptPasswordEncoder) passwordEncoder;
        this.tutorLegalMapper = tutorLegalMapper;
        this.asistenciaService = asistenciaService;
        this.estudianteRepository = estudianteRepository;
    }

    @Override
    @Transactional
    public TutorLegalResponseDTO save(TutorLegalRequestDTO tutorLegalRequestDTO) {
        try {
            // Validar datos únicos
            validateUniqueFields(tutorLegalRequestDTO);

            // Validar contraseña
            validarPassword(tutorLegalRequestDTO.contrasena());

            // Crear y configurar el nuevo tutor
            TutorLegal tutorLegal = tutorLegalMapper.toEntity(tutorLegalRequestDTO);
            tutorLegal.setContrasena(passwordEncoder.encode(tutorLegalRequestDTO.contrasena()));
            tutorLegal.setActivo(true);
            tutorLegal.setRol(RoleEnum.ROLE_TUTOR);

            // Guardar el tutor primero para obtener su ID
            TutorLegal savedTutor = tutorLegalRepository.save(tutorLegal);

            // Procesar la asignación de estudiantes si se proporcionan
            if (tutorLegalRequestDTO.estudiante() != null && !tutorLegalRequestDTO.estudiante().isEmpty()) {
                // Obtener los estudiantes a asignar
                List<Estudiante> estudiantes = estudianteRepository.findAllById(tutorLegalRequestDTO.estudiante());

                // Validar que se encontraron todos los estudiantes
                if (estudiantes.size() != tutorLegalRequestDTO.estudiante().size()) {
                    throw new ApplicationException("Uno o más estudiantes no fueron encontrados");
                }

                // Validar y asignar cada estudiante
                for (Estudiante estudiante : estudiantes) {
                    // Verificar si el estudiante ya tiene un tutor
                    if (estudiante.getTutor() != null) {
                        throw new ApplicationException(
                                String.format("El estudiante con ID %d ya tiene asignado el tutor con ID %d",
                                        estudiante.getId(),
                                        estudiante.getTutor().getId())
                        );
                    }

                    // Asignar el nuevo tutor
                    estudiante.setTutor(savedTutor);
                    estudianteRepository.save(estudiante);
                }

                // Recargar el tutor con los estudiantes actualizados
                savedTutor = tutorLegalRepository.findByUserId(savedTutor.getId())
                        .orElseThrow(() -> new ApplicationException("Error al recargar el tutor"));
            }

            return tutorLegalMapper.toResponseDTO(savedTutor);

        } catch (Exception e) {
            throw new ApplicationException("Error al guardar el tutor: " + e.getMessage());
        }
    }

//    @Transactional
//    @Override
//    public TutorLegalResponseDTO save(TutorLegalRequestDTO tutorLegalRequestDTO) {
//        try {
//            validateUniqueFields(tutorLegalRequestDTO);
//
//            if (tutorLegalRepository.findByEmail(tutorLegalRequestDTO.email()).isPresent()) {
//                throw new ApplicationException("Tutor legal con email ya existente: " + tutorLegalRequestDTO.email());
//            }
//            TutorLegal newTutorLegal = tutorLegalMapper.toEntity(tutorLegalRequestDTO);
//            newTutorLegal.setContrasena(passwordEncoder.encode(tutorLegalRequestDTO.contrasena()));
//            newTutorLegal.setRol(RoleEnum.valueOf("ROLE_TUTOR"));
//            newTutorLegal.setActivo(true);
//            newTutorLegal = tutorLegalRepository.save(newTutorLegal);
//            return tutorLegalMapper.toResponseDTO(newTutorLegal);
//        } catch (ApplicationException e) {
//            throw new ApplicationException("Error al guardar el usuario: " + e.getMessage());
//        }
//    }



    @Override
    public Optional<TutorLegalResponseDTO> findById(Long id) {
        Optional<TutorLegal> tutorLegal = tutorLegalRepository.findById(id);
        return tutorLegal.map(tutorLegalMapper::toResponseDTO);
    }


    @Override
    @Transactional
    public TutorLegalResponseDTO update(TutorLegalRequestDTO tutorLegalRequestDTO) {
        // Buscar el tutor por el ID de usuario
        Optional<TutorLegal> tutorOptional = tutorLegalRepository.findByUserId(tutorLegalRequestDTO.id());

        if (tutorOptional.isEmpty()) {
            throw new ApplicationException("Tutor legal no encontrado");
        }

        TutorLegal tutorLegal = tutorOptional.get();

        // Actualizar campos básicos
        TutorLegal tutorLegalUpdated = (TutorLegal) UpdatedEntities.update(tutorLegal, tutorLegalRequestDTO);

        // Actualizar contraseña si se proporciona
        if (tutorLegalRequestDTO.contrasena() != null) {
            validarPassword(tutorLegalRequestDTO.contrasena());
            tutorLegalUpdated.setContrasena(passwordEncoder.encode(tutorLegalRequestDTO.contrasena()));
        }

        // Primero guardamos el tutor para asegurar que tenga un ID válido
        TutorLegal savedTutor = tutorLegalRepository.save(tutorLegalUpdated);

        // Manejar la asignación de estudiantes si se proporcionan
        if (tutorLegalRequestDTO.estudiante() != null && !tutorLegalRequestDTO.estudiante().isEmpty()) {
            try {
                // Obtener los estudiantes actuales del tutor
                List<Estudiante> estudiantesActuales = estudianteRepository.findByTutorUserId(savedTutor.getId());

                // Obtener los nuevos estudiantes
                List<Estudiante> nuevosEstudiantes = estudianteRepository.findAllById(tutorLegalRequestDTO.estudiante());

                if (nuevosEstudiantes.size() != tutorLegalRequestDTO.estudiante().size()) {
                    throw new ApplicationException("Uno o más estudiantes no fueron encontrados");
                }

                // Desvincular estudiantes que ya no están en la lista
                estudiantesActuales.forEach(estudiante -> {
                    if (!tutorLegalRequestDTO.estudiante().contains(estudiante.getId())) {
                        estudiante.setTutor(null);
                        estudianteRepository.save(estudiante);
                    }
                });

                // Asignar nuevos estudiantes
                for (Estudiante estudiante : nuevosEstudiantes) {
                    if (estudiante.getTutor() != null &&
                            !estudiante.getTutor().getId().equals(savedTutor.getId())) {
                        throw new ApplicationException(
                                String.format("El estudiante con ID %d ya tiene asignado el tutor con ID %d",
                                        estudiante.getId(),
                                        estudiante.getTutor().getId())
                        );
                    }
                    estudiante.setTutor(savedTutor);
                    estudianteRepository.save(estudiante);
                }
            } catch (Exception e) {
                throw new ApplicationException("Error al actualizar los estudiantes: " + e.getMessage());
            }
        }

        return tutorLegalMapper.toResponseDTO(savedTutor);
    }


//    @Override
//    @Transactional
//    public TutorLegalResponseDTO update(TutorLegalRequestDTO tutorLegalRequestDTO) {
//
//        Optional<TutorLegal> tutorLegal = tutorLegalRepository.findById(tutorLegalRequestDTO.id());
//
//        if (tutorLegal.isPresent()) {
//            TutorLegal tutorLegalUpdated = (TutorLegal) UpdatedEntities.update(tutorLegal.get(), tutorLegalRequestDTO);
//            if(tutorLegalRequestDTO.contrasena() != null){
//                validarPassword(tutorLegalRequestDTO.contrasena());
//                tutorLegalUpdated.setContrasena(passwordEncoder.encode(tutorLegalRequestDTO.contrasena()));
//            }
//            return tutorLegalMapper.toResponseDTO(tutorLegalRepository.save(tutorLegalUpdated));
//        } else
//        {
//            throw new ApplicationException("Tutor legal no encontrado");
//        }
//}


    @Override
    public Iterable<TutorLegalResponseDTO> findAll() {
        List<TutorLegal> tutorLegal = tutorLegalRepository.findAll();
        return tutorLegal.stream().map(tutorLegalMapper::toResponseDTO)
                .toList();
    }

    @Override
    public void deleteById(Long aLong) {
        TutorLegal tutorLegal = tutorLegalRepository.findById(aLong)
                .orElseThrow(() -> new ApplicationException("Tutor legal no encontrado"));
        tutorLegalRepository.delete(tutorLegal);
    }


    @Override
    public Iterable<AsistenciaDTO> findAsistenciasByEstudianteId(Long idTutor, Long gradoId) {
        Optional<TutorLegal> response = tutorLegalRepository.findById(idTutor);
        if (response.isEmpty()) {
            throw new ApplicationException("Tutor legal no encontrado");
        }
        return asistenciaService.getAsistenciasByGradoAndEstudiante(idTutor,gradoId);

    }

    // Este metodo es redudante ya que el el dto se aplica
    protected void validarPassword(String contrasena) {
        if (contrasena!=null && contrasena.length() < 8) {
            throw new ApplicationException("La contraseña debe tener al menos 8 caracteres");
        }
    }

    /**
     * Verifica si un tutor tiene estudiantes activos
     */
    @Override
    public boolean hasActiveStudents(Long tutorId) {
        return tutorLegalRepository.existsByIdAndEstudiantesActivoTrue(tutorId);
    }
    private void validateUniqueFields(TutorLegalRequestDTO dto) {
        if (tutorLegalRepository.existsByDni(dto.dni())) {
            throw new ApplicationException("Ya existe un tutor con el DNI proporcionado");
        }
        if (tutorLegalRepository.findByEmail(dto.email()).isPresent()) {
            throw new ApplicationException("Ya existe un tutor con el email proporcionado");
        }
    }

//    private void validateUniqueFieldsForUpdate(TutorLegalRequestDTO dto, Long currentId) {
//        tutorLegalRepository.findByEmail(dto.email())
//                .ifPresent(tutor -> {
//                    if (!tutor.getId().equals(currentId)) {
//                        throw new ApplicationException("El email ya está en uso por otro tutor");
//                    }
//                });
//    }


}
