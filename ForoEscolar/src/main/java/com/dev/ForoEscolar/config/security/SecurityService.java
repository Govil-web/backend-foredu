package com.dev.ForoEscolar.config.security;

import com.dev.ForoEscolar.model.Asistencia;
import com.dev.ForoEscolar.model.User;
import com.dev.ForoEscolar.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.dev.ForoEscolar.enums.RoleEnum.ROLE_ADMINISTRADOR;

@Service
public class SecurityService {


    private final UserRepository userRepository;
    private final EstudianteRepository estudianteRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final GradoRepository gradoRepository;
    private final TutorLegalRepository tutorLegalRepository;

    @Autowired
    public SecurityService(UserRepository userRepository, EstudianteRepository estudianteRepository,
                           AsistenciaRepository asistenciaRepository, GradoRepository gradoRepository,
                           TutorLegalRepository tutorLegalRepository) {
        this.userRepository = userRepository;
        this.estudianteRepository = estudianteRepository;
        this.asistenciaRepository = asistenciaRepository;
        this.gradoRepository = gradoRepository;
        this.tutorLegalRepository = tutorLegalRepository;
    }


    public boolean hasAccessToInformation(Long requestedUserId)  {
        // Obtener el usuario autenticado actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Si el usuario solicita su propia información
        if (currentUser.getId().equals(requestedUserId)) {
            return false;
        }

        // Validar según el rol
        return !switch (currentUser.getRol()) {
            case ROLE_TUTOR ->
                // Verificar si el estudiante solicitado está asociado al tutor
                    estudianteRepository.existsByIdAndTutorId(requestedUserId, currentUser.getId());
            case ROLE_PROFESOR ->
                // Verificar si el estudiante está en alguna de las clases del profesor
                    estudianteRepository.existsByIdAndProfesores(requestedUserId, currentUser.getId());
            case ROLE_ESTUDIANTE ->
                // Un estudiante solo puede ver su propia información
                    false;
            case ROLE_ADMINISTRADOR ->
                // El admin puede ver toda la información
                    true;

        };
    }
/*
         * Verifica si un usuario puede gestionar asistencias en un grado específico
     */
    public boolean canManageGradeAttendance(Long userId, Long gradoId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return switch (user.getRol()) {
            case ROLE_ADMINISTRADOR -> true;
            case ROLE_PROFESOR ->
                // Verificar si el profesor está asignado al grado
                    gradoRepository.existsByIdAndProfesorId(gradoId, userId);
            default -> false;
        };
    }

    /**
     * Verifica si un usuario puede ver una asistencia específica
     */
    public boolean canViewAttendance(Long userId, Long asistenciaId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return switch (user.getRol()) {
            case ROLE_ADMINISTRADOR -> true;
            case ROLE_PROFESOR -> {
                // Verificar si el profesor registró la asistencia o está asignado al grado
                Asistencia asistencia = asistenciaRepository.findById(asistenciaId)
                        .orElseThrow(() -> new RuntimeException("Asistencia no encontrada"));
                yield asistencia.getProfesor().getId().equals(userId) ||
                        gradoRepository.existsByIdAndProfesorId(asistencia.getGrado().getId(), userId);
            }
            case ROLE_TUTOR ->
                // Verificar si el tutor tiene estudiantes en el grado de la asistencia
                    asistenciaRepository.existsByIdAndEstudianteTutorId(asistenciaId, userId);
            default -> false;
        };
    }

    /**
     * Verifica si un usuario puede ver asistencias de un grado
     */
    public boolean canViewGradeAttendance(Long userId, Long gradoId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return !switch (user.getRol()) {
            case ROLE_ADMINISTRADOR -> true;
            case ROLE_PROFESOR ->
                // Verificar si el profesor está asignado al grado
                    gradoRepository.existsByIdAndProfesorId(gradoId, userId);
            case ROLE_TUTOR ->
                // Verificar si el tutor tiene estudiantes en el grado
                    gradoRepository.existsByIdAndEstudiantesTutorId(gradoId, userId);
            default -> false;
        };
    }

    /**
     * Verifica si un usuario es administrador
     */
    public boolean isAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return ROLE_ADMINISTRADOR.equals(user.getRol());
    }

    /**
     * Verifica si un usuario puede actualizar una asistencia
     */
    public boolean canUpdateAttendance(Long userId, Long asistenciaId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return switch (user.getRol()) {
            case ROLE_ADMINISTRADOR -> true;
            case ROLE_PROFESOR ->
                // Solo el profesor que registró la asistencia puede actualizarla
                    asistenciaRepository.existsByIdAndProfesorId(asistenciaId, userId);
            default -> false;
        };
    }

    /**
     * Verifica si un usuario puede acceder a la información de un tutor
     */
    public boolean canAccessTutorInfo(Long userId, Long tutorId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return switch (user.getRol().name()) {
            case "ROLE_ADMINISTRADOR" -> true;
            case "ROLE_TUTOR" -> userId.equals(tutorId);
            case "ROLE_PROFESOR" -> tutorLegalRepository.existsByIdAndEstudiantesProfesorId(tutorId, userId);
            default -> false;
        };
    }

    /**
     * Verifica si un usuario puede actualizar la información de un tutor
     */
    public boolean canUpdateTutor(Long userId, Long tutorId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return switch (user.getRol().name()) {
            case "ROLE_ADMINISTRADOR" -> true;
            case "ROLE_TUTOR" -> userId.equals(tutorId);
            default -> false;
        };
    }

    /**
     * Verifica si un tutor tiene estudiantes activos asociados
     */
    public boolean hasActiveStudents(Long tutorId) {
        return tutorLegalRepository.existsByIdAndEstudiantesActivoTrue(tutorId);
    }

    /**
     * Verifica si un usuario puede ver un grado específico
     * @param userId iD del usuario que intenta ver el grado
     * @param gradoId iD del grado que se intenta ver
     * @return true si tiene permiso, false en caso contrario
     */
    public boolean canViewGrade(Long userId, Long gradoId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Switch por rol del usuario
        return switch (user.getRol().name()) {
            case "ROLE_ADMINISTRADOR" -> true;

            case "ROLE_PROFESOR" -> // Verificar si el profesor está asignado al grado
                    gradoRepository.existsByIdAndProfesorId(gradoId, userId);

            case "ROLE_TUTOR" -> // Verificar si el tutor tiene estudiantes en ese grado
                    gradoRepository.existsByIdAndEstudiantesTutorId(gradoId, userId);

            case "ROLE_ESTUDIANTE" -> // Un estudiante solo puede ver su propio grado
                    gradoRepository.existsByIdAndEstudiantesId(gradoId, userId);

            default -> false;
        };
    }

}
