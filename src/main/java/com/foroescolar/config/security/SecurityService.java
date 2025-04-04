package com.foroescolar.config.security;

import com.foroescolar.dtos.user.UserPrincipal;
import com.foroescolar.dtos.user.UserResponseDTO;
import com.foroescolar.model.Asistencia;
import com.foroescolar.model.User;
import com.foroescolar.repository.*;
import com.foroescolar.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.foroescolar.enums.RoleEnum.ROLE_ADMINISTRADOR;

@Slf4j
@Service
public class SecurityService {


    private final UserRepository userRepository;
    private final EstudianteRepository estudianteRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final GradoRepository gradoRepository;
    private final TutorLegalRepository tutorLegalRepository;
    private final UserService userService;

    private static final String USUARIO_NO_ENCONTRADO = "Usuario no encontrado";

    @Autowired
    public SecurityService(UserRepository userRepository, EstudianteRepository estudianteRepository,
                           AsistenciaRepository asistenciaRepository, GradoRepository gradoRepository,
                           TutorLegalRepository tutorLegalRepository, UserService userService) {
        this.userRepository = userRepository;
        this.estudianteRepository = estudianteRepository;
        this.asistenciaRepository = asistenciaRepository;
        this.gradoRepository = gradoRepository;
        this.tutorLegalRepository = tutorLegalRepository;
        this.userService = userService;
    }

    public UserPrincipal getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        UserResponseDTO user = userService.findByEmail(userDetails.getUsername());
        return new UserPrincipal(user.id(), user.email());
    }


    public boolean hasAccessToInformation(Long requestedUserId)  {
        // Obtener el usuario autenticado actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(USUARIO_NO_ENCONTRADO));

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
                    estudianteRepository.existsByIdAndTutorId(requestedUserId, currentUser.getId());
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
                .orElseThrow(() -> new UsernameNotFoundException(USUARIO_NO_ENCONTRADO));

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
                .orElseThrow(() -> new UsernameNotFoundException(USUARIO_NO_ENCONTRADO));

        return switch (user.getRol()) {
            case ROLE_ADMINISTRADOR -> true;
            case ROLE_PROFESOR -> {
                // Verificar si el profesor registró la asistencia o está asignado al grado
                Asistencia asistencia = asistenciaRepository.findById(asistenciaId)
                        .orElseThrow(() -> new RuntimeException("Asistencia no encontrada"));
                yield  gradoRepository.existsByIdAndProfesorId(asistencia.getGrado().getId(), userId);
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
                .orElseThrow(() -> new UsernameNotFoundException(USUARIO_NO_ENCONTRADO));

        return switch (user.getRol()) {
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
                .orElseThrow(() -> new UsernameNotFoundException(USUARIO_NO_ENCONTRADO));
        return !ROLE_ADMINISTRADOR.equals(user.getRol());
    }

    /**
     * Verifica si un usuario puede actualizar una asistencia
     */
    public boolean canUpdateAttendance(Long userId, Long gradoId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(USUARIO_NO_ENCONTRADO));

        return switch (user.getRol()) {
            case ROLE_ADMINISTRADOR -> true;
            case ROLE_PROFESOR ->
                // Solo el profesor que registró la asistencia puede actualizarla
                    gradoRepository.existsByIdAndProfesorId(gradoId, userId);
            default -> false;
        };
    }

    /**
     * Verifica si un usuario puede acceder a la información de un tutor legal
     * @param userId ID del usuario que intenta acceder
     * @param tutorId ID del tutor al que se intenta acceder
     * @return true si tiene acceso permitido, false en caso contrario
     */
    public boolean canAccessTutorInfo(Long userId, Long tutorId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(USUARIO_NO_ENCONTRADO));

        // Verificar si es el mismo usuario intentando acceder a su información
        if (user.getId().equals(tutorId)) {
            log.debug("Usuario {} accediendo a su propia información", userId);
            return true;
        }

        return switch (user.getRol()) {
            case ROLE_ADMINISTRADOR -> true;
            case ROLE_PROFESOR -> tutorLegalRepository.existsByIdAndEstudiantesProfesorId(tutorId, userId);
            default -> false;
        };
    }

    /**
     * Verifica si un usuario puede actualizar la información de un tutor
     */
    public boolean canUpdateTutor(Long userId, Long tutorId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(USUARIO_NO_ENCONTRADO));

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
                .orElseThrow(() -> new UsernameNotFoundException(USUARIO_NO_ENCONTRADO));

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

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(USUARIO_NO_ENCONTRADO));

        return currentUser.getId();
    }

    public boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(USUARIO_NO_ENCONTRADO));

        return ROLE_ADMINISTRADOR.equals(currentUser.getRol());
    }
}
