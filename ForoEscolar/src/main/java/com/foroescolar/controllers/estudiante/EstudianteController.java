package com.foroescolar.controllers.estudiante;

import com.foroescolar.config.security.SecurityService;
import com.foroescolar.controllers.ApiResponse;
import com.foroescolar.dtos.ApiResponseDto;
import com.foroescolar.dtos.asistencia.AsistenciaDTO;
import com.foroescolar.dtos.estudiante.EstudiantePerfilDto;
import com.foroescolar.dtos.estudiante.EstudianteResponseDTO;
import com.foroescolar.dtos.user.UserPrincipal;
import com.foroescolar.dtos.user.UserResponseDTO;
import com.foroescolar.exceptions.ApplicationException;
import com.foroescolar.exceptions.model.DniDuplicadoException;
import com.foroescolar.exceptions.model.ForbiddenException;
import com.foroescolar.services.EstudianteService;
import com.foroescolar.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/estudiante")
@Slf4j
@RequiredArgsConstructor
public class EstudianteController {

    private final EstudianteService estudianteService;
    private final SecurityService securityService;
    private final UserService userService;

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un estudiante por ID")
    public ResponseEntity<ApiResponseDto<EstudianteResponseDTO>> findById(@PathVariable Long id) {
        validateAccess(id);
        return estudianteService.findById(id)
                .map(estudiante -> ApiResponse.success("Estudiante encontrado", estudiante))
                .orElse(ApiResponse.notFound("Estudiante no encontrado"));
    }
    @GetMapping("getAll")
    @Operation(summary = "Obtiene todos los estudiantes")
    public ResponseEntity<ApiResponseDto<List<EstudianteResponseDTO>>> getAllEstudiantes() {
        List<EstudianteResponseDTO> estudiantes = (List<EstudianteResponseDTO>) estudianteService.findAll();
        return ApiResponse.success("Estudiantes recuperados exitosamente", estudiantes);
    }


    @PostMapping("/add")
    @Operation(summary = "Agregar nuevo estudiante")
    public ResponseEntity<ApiResponseDto<EstudianteResponseDTO>> save(
            @Valid @RequestBody EstudianteResponseDTO dto) {
//        UserPrincipal currentUser = getCurrentUser();
//        validateAdminAccess(currentUser.id());

        try {
            EstudianteResponseDTO estudiante = estudianteService.save(dto);
            log.info("Estudiante creado. ID: {}", estudiante.id());
            return ApiResponse.created("Estudiante registrado exitosamente", estudiante);
        } catch (DniDuplicadoException ex) {
            log.warn("DNI duplicado: {}", dto.dni());
            return ApiResponse.conflict(ex.getMessage());
        }
    }

    @PutMapping("/update")
    @Operation(summary = "Actualiza un estudiante")
    public ResponseEntity<ApiResponseDto<EstudianteResponseDTO>> update(
            @Valid @RequestBody EstudianteResponseDTO dto) {

        validateAccess(dto.id());

        EstudianteResponseDTO estudiante = estudianteService.update(dto);
        return ApiResponse.success("Estudiante actualizado exitosamente", estudiante);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Se elimina un estudiante en particular")
    public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable("id") Long id) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserResponseDTO user = userService.findByEmail(userDetails.getUsername());

            if (!securityService.isAdmin(user.id())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponseDto<>(false, "Solo los administradores pueden eliminar estudiantes", null));
            }

            estudianteService.deleteById(id);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Estudiante eliminado exitosamente", null));
        } catch (ApplicationException e) {
            throw new ApplicationException("", "Error al listar los boletines", e.getHttpStatus());
        }
    }

    @GetMapping("/filterGrado")
    @Operation(summary = "Se filtra a los estudiantes por grado")
    public ResponseEntity<ApiResponseDto<EstudiantePerfilDto>> filtroXGrado(@RequestParam("gradoId") Long gradoId) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserResponseDTO user = userService.findByEmail(userDetails.getUsername());

            if (!securityService.canViewGradeAttendance(user.id(), gradoId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponseDto<>(false, "No tienes permisos para ver los estudiantes de este grado", null));
            }

            List<EstudiantePerfilDto> estudianteResponseDTOS = estudianteService.findByGradoId(gradoId);
            if (estudianteResponseDTOS.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponseDto<>(true, "No hay estudiantes asignados al grado", null));
            }
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Exito", estudianteResponseDTOS));
        } catch (ApplicationException e) {
            throw new ApplicationException("", "Error al listar los boletines", e.getHttpStatus());
        }
    }

    @GetMapping("/{id}/asistencias")
    @Operation(summary = "Obtiene la lista de asistencias de un estudiante por su ID")
    public ResponseEntity<ApiResponseDto<AsistenciaDTO>> findAsistenciasByEstudianteId(@PathVariable("id") Long id) {
        try {


            if (securityService.hasAccessToInformation(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponseDto<>(false, "No tienes permisos para ver las asistencias de este estudiante", null));
            }

            List<AsistenciaDTO> asistencias = estudianteService.findByEstudianteId(id);
            if (asistencias.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponseDto<>(true, "No hay asistencias para el estudiante", null));
            }
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Exito", asistencias));
        } catch (ApplicationException e) {
            throw new ApplicationException("Ha ocurrido un error: ",e.getMessage() , e.getHttpStatus());
        }
    }

    @PostMapping("/{id}/isEnable")
    @Operation(summary = "Se da de baja o alta a un estudiante")
    public ResponseEntity<ApiResponseDto<Void>> unsubscribe(@PathVariable("id") Long id) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserResponseDTO user = userService.findByEmail(userDetails.getUsername());
            if (!securityService.isAdmin(user.id())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponseDto<>(false, "Solo administradores pueden dar de baja a un estudiante", null));
            }
            boolean state = estudianteService.subscribe_unsubscribe(id);
            if (state) {
                return ResponseEntity.ok(new ApiResponseDto<>(true, "Estudiante dado de alta exitosamente", null));
            }
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Estudiante dado de baja exitosamente", null));

        } catch (ApplicationException e) {
            throw new ApplicationException("Error actualizar el estado del estudiante: ",e.getMessage() , e.getHttpStatus());
        }
    }

    private UserPrincipal getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        UserResponseDTO user = userService.findByEmail(userDetails.getUsername());
        return new UserPrincipal(user.id(), user.email());
    }

    private void validateAccess(Long resourceId) {
        if (securityService.hasAccessToInformation(resourceId)) {
            throw new ForbiddenException("No tiene permisos para acceder a este recurso");
        }
    }

    private void validateAdminAccess(Long userId) {
        if (!securityService.isAdmin(userId)) {
            throw new ForbiddenException("Solo administradores pueden realizar esta operación");
        }
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleForbiddenException(ForbiddenException ex) {
        return ApiResponse.forbidden(ex.getMessage());
    }
}