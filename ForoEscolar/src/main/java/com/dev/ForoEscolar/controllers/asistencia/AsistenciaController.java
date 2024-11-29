package com.dev.ForoEscolar.controllers.asistencia;


import com.dev.ForoEscolar.config.security.SecurityService;
import com.dev.ForoEscolar.dtos.ApiResponseDto;
import com.dev.ForoEscolar.dtos.asistencia.AsistenciaDTO;
import com.dev.ForoEscolar.dtos.asistencia.AsistenciaRequestDto;
import com.dev.ForoEscolar.dtos.user.UserResponseDTO;
import com.dev.ForoEscolar.exceptions.ApplicationException;
import com.dev.ForoEscolar.services.AsistenciaService;
import com.dev.ForoEscolar.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("api/asistencia")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;
    private final UserService userService;
    private final SecurityService securityService;

    @Autowired
    public AsistenciaController(
            AsistenciaService asistenciaService,
            UserService userService,
            SecurityService securityService) {
        this.asistenciaService = asistenciaService;
        this.userService = userService;
        this.securityService = securityService;
    }

    @PostMapping("/add")
    @Operation(summary = "Register a new asistencia")
    public ResponseEntity<ApiResponseDto<AsistenciaDTO>> registerAsistencia(@RequestBody AsistenciaRequestDto asistenciaDTO) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserResponseDTO user = userService.findByEmail(userDetails.getUsername());

            // Verificar si el profesor tiene permiso para registrar asistencia en este grado
            if (securityService.canManageGradeAttendance(user.id(), asistenciaDTO.getGrado())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponseDto<>(false, "No tienes permiso para registrar asistencia en este grado", null));
            }

            asistenciaDTO.setProfesor(user.id());
            AsistenciaDTO asistencia = asistenciaService.save(asistenciaDTO);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Asistencia guardada exitosamente", asistencia));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(false, "Error al registrar asistencia: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get asistencia by id")
    public ResponseEntity<ApiResponseDto<AsistenciaDTO>> getAsistenciaById(@PathVariable Long id) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserResponseDTO user = userService.findByEmail(userDetails.getUsername());

            // Verificar si tiene permiso para ver esta asistencia específica
            if (!securityService.canViewAttendance(user.id(), id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponseDto<>(false, "No tienes permiso para ver esta asistencia", null));
            }

            return asistenciaService.findById(id)
                    .map(asistencia -> ResponseEntity.ok(new ApiResponseDto<>(true, "Asistencia encontrada", asistencia)))
                    .orElseGet(() -> ResponseEntity.badRequest()
                            .body(new ApiResponseDto<>(false, "Asistencia no encontrada", null)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(false, "Error al buscar asistencia: " + e.getMessage(), null));
        }
    }

    @GetMapping("/grado/{id}")
    @Operation(summary = "Get asistencia by grado")
    public ResponseEntity<ApiResponseDto<AsistenciaDTO>> getAsistenciaBygrado(@PathVariable Long id) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserResponseDTO user = userService.findByEmail(userDetails.getUsername());

            // Verificar si tiene permiso para ver asistencias del grado
            if (securityService.canViewGradeAttendance(user.id(), id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponseDto<>(false, "No tienes permiso para ver las asistencias de este grado", null));
            }

            Iterable<AsistenciaDTO> listarAsistencias = asistenciaService.getAsistenciasByGrado(id);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Asistencias encontradas", listarAsistencias));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/getAll")
    @Operation(summary = "List all asistencias")
    public ResponseEntity<ApiResponseDto<AsistenciaDTO>> asistenciasList() {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserResponseDTO user = userService.findByEmail(userDetails.getUsername());

            // Solo administradores pueden ver todas las asistencias
            if (securityService.isAdmin(user.id())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponseDto<>(false, "Solo los administradores pueden ver todas las asistencias", null));
            }

            Iterable<AsistenciaDTO> listarAsistencias = asistenciaService.findAll();
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Lista de asistencias", listarAsistencias));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(false, "Error al listar asistencias: " + e.getMessage(), null));
        }
    }

    @GetMapping("/fechaYgrado/{id}")
    @Operation(summary = "List all asistencias for a specific date and grade")
    public ResponseEntity<ApiResponseDto<AsistenciaDTO>> getAsistenciasByDateAndGrado(
            @PathVariable Long id,
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserResponseDTO user = userService.findByEmail(userDetails.getUsername());


            if (securityService.canViewGradeAttendance(user.id(), id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponseDto<>(false, "No tienes permiso para ver las asistencias de este grado", null));
            }

            Iterable<AsistenciaDTO> listarAsistencias = asistenciaService.getByFechaBeetweenAndGrado(id, fechaInicio, fechaFin);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Asistencias encontradas", listarAsistencias));
        } catch (ApplicationException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update asistencia")
    public ResponseEntity<ApiResponseDto<AsistenciaDTO>> updateAsistencia(
            @PathVariable Long id,
            @RequestBody AsistenciaRequestDto asistenciaDTO) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserResponseDTO user = userService.findByEmail(userDetails.getUsername());

            // Verificar si tiene permiso para actualizar esta asistencia
            if (!securityService.canUpdateAttendance(user.id(), id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponseDto<>(false, "No tienes permiso para actualizar esta asistencia", null));
            }

            asistenciaService.update(asistenciaDTO);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Asistencia actualizada exitosamente", null));
        } catch (ApplicationException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }
}