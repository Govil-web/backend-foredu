package com.dev.ForoEscolar.controllers.estudiante;

import com.dev.ForoEscolar.config.security.SecurityService;
import com.dev.ForoEscolar.dtos.ApiResponseDto;
import com.dev.ForoEscolar.dtos.asistencia.AsistenciaDTO;
import com.dev.ForoEscolar.dtos.estudiante.EstudianteResponseDTO;
import com.dev.ForoEscolar.dtos.user.UserResponseDTO;
import com.dev.ForoEscolar.exceptions.ApplicationException;
import com.dev.ForoEscolar.services.EstudianteService;
import com.dev.ForoEscolar.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/estudiante")
public class EstudianteController {

    private final EstudianteService estudianteService;
    private final SecurityService securityService;
    private final UserService userService;

    @Autowired
    public EstudianteController(EstudianteService estudianteService,
                                SecurityService securityService,
                                UserService userService) {
        this.estudianteService = estudianteService;
        this.securityService = securityService;
        this.userService = userService;
    }

    @GetMapping("/getAll")
    @Operation(summary = "Obtiene todos los estudiantes")
    public ResponseEntity<ApiResponseDto<EstudianteResponseDTO>> findAll() {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserResponseDTO user = userService.findByEmail(userDetails.getUsername());

            if (securityService.isAdmin(user.id())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponseDto<>(false, "No tienes permisos para ver todos los estudiantes", null));
            }

            Iterable<EstudianteResponseDTO> list = estudianteService.findAll();
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Exito", list));
        } catch (ApplicationException e) {
            throw new ApplicationException("Ha ocurrido un error " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un estudiante en particular")
    public ResponseEntity<ApiResponseDto<EstudianteResponseDTO>> findById(@PathVariable("id") Long id) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserResponseDTO user = userService.findByEmail(userDetails.getUsername());

            if (securityService.hasAccessToInformation(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponseDto<>(false, "No tienes permisos para ver este estudiante", null));
            }

            Optional<EstudianteResponseDTO> estudiante = estudianteService.findById(id);
            return estudiante.map(estudianteResponseDTO -> ResponseEntity.ok(new ApiResponseDto<>(true, "Estudiante encontrado", estudianteResponseDTO))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(false, "Estudiante no encontrado", null)));
        } catch (Exception e) {
            throw new ApplicationException("Ha ocurrido un error: " + e.getMessage());
        }
    }

    @PostMapping("/add")
    @Operation(summary = "Se agrega un estudiante")
    public ResponseEntity<ApiResponseDto<EstudianteResponseDTO>> save(@RequestBody @Valid EstudianteResponseDTO dto) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserResponseDTO user = userService.findByEmail(userDetails.getUsername());

            if (securityService.isAdmin(user.id())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponseDto<>(false, "Solo los administradores pueden agregar estudiantes", null));
            }

            EstudianteResponseDTO estudiante = estudianteService.save(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponseDto<>(true, "Estudiante guardado exitosamente", estudiante));
        } catch (Exception e) {
            throw new ApplicationException("Error al guardar estudiante: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    @Operation(summary = "Se actualiza un estudiante en particular")
    public ResponseEntity<ApiResponseDto<EstudianteResponseDTO>> update(@RequestBody EstudianteResponseDTO dto) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserResponseDTO user = userService.findByEmail(userDetails.getUsername());

            if (securityService.hasAccessToInformation(dto.id())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponseDto<>(false, "No tienes permisos para actualizar este estudiante", null));
            }

            EstudianteResponseDTO estudiante = estudianteService.update(dto);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Estudiante actualizado exitosamente", estudiante));
        } catch (Exception e) {
            throw new ApplicationException("Error al actualizar estudiante: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Se elimina un estudiante en particular")
    public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable("id") Long id) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserResponseDTO user = userService.findByEmail(userDetails.getUsername());

            if (securityService.isAdmin(user.id())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponseDto<>(false, "Solo los administradores pueden eliminar estudiantes", null));
            }

            estudianteService.deleteById(id);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Estudiante eliminado exitosamente", null));
        } catch (Exception e) {
            throw new ApplicationException("Error al eliminar estudiante: " + e.getMessage());
        }
    }

    @GetMapping("/filterGrado")
    @Operation(summary = "Se filtra a los estudiantes por grado")
    public ResponseEntity<ApiResponseDto<EstudianteResponseDTO>> filtroXGrado(@RequestParam Long gradoId) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserResponseDTO user = userService.findByEmail(userDetails.getUsername());

            if (!securityService.canViewGradeAttendance(user.id(), gradoId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponseDto<>(false, "No tienes permisos para ver los estudiantes de este grado", null));
            }

            List<EstudianteResponseDTO> estudianteResponseDTOS = estudianteService.findByGradoId(gradoId);
            if (estudianteResponseDTOS.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponseDto<>(true, "No hay estudiantes asignados al grado", null));
            }
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Exito", estudianteResponseDTOS));
        } catch (Exception e) {
            throw new ApplicationException("Ha ocurrido un error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/asistencias")
    @Operation(summary = "Obtiene la lista de asistencias de un estudiante por su ID")
    public ResponseEntity<ApiResponseDto<AsistenciaDTO>> findAsistenciasByEstudianteId(@PathVariable("id") Long id) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserResponseDTO user = userService.findByEmail(userDetails.getUsername());

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
        } catch (Exception e) {
            throw new ApplicationException("Ha ocurrido un error: " + e.getMessage());
        }
    }
}