package com.dev.ForoEscolar.controllers.grado;

import com.dev.ForoEscolar.config.security.SecurityService;
import com.dev.ForoEscolar.dtos.ApiResponseDto;
import com.dev.ForoEscolar.dtos.grado.GradoDto;
import com.dev.ForoEscolar.dtos.user.UserResponseDTO;
import com.dev.ForoEscolar.exceptions.ApplicationException;
import com.dev.ForoEscolar.services.GradoService;
import com.dev.ForoEscolar.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/grado")
@Tag(name = "Grado", description = "Endpoints para gestión de grados")
public class GradoController {

    private final GradoService gradoService;
    private final UserService userService;
    private final SecurityService securityService;

    @Autowired
    public GradoController(GradoService gradoService,
                           UserService userService,
                           SecurityService securityService) {
        this.gradoService = gradoService;
        this.userService = userService;
        this.securityService = securityService;
    }

    @PostMapping("/register")
    @Operation(summary = "Registra un nuevo grado")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<ApiResponseDto<GradoDto>> registerGrado(@RequestBody @Valid GradoDto gradoDto) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserResponseDTO user = userService.findByEmail(userDetails.getUsername());

            if (!user.rol().equals("ROLE_ADMINISTRADOR")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponseDto<>(false, "Solo los administradores pueden registrar grados", null));
            }

            GradoDto gradoCreado = gradoService.save(gradoDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponseDto<>(true, "Grado creado exitosamente", gradoCreado));
        } catch (Exception e) {
            throw new ApplicationException("Error al registrar grado: " + e.getMessage());
        }
    }

    @GetMapping("/getAll")
    @Operation(summary = "Obtiene todos los grados")
    public ResponseEntity<ApiResponseDto<GradoDto>> getAll() {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserResponseDTO user = userService.findByEmail(userDetails.getUsername());

            // El ADMINISTRADOR puede ver todos los grados
            // PROFESOR puede ver los grados asignados
            // TUTOR puede ver los grados donde tiene estudiantes
            Iterable<GradoDto> grados;
            switch (user.rol()) {
                case "ROLE_ADMINISTRADOR" -> grados = gradoService.findAll();
                case "ROLE_PROFESOR" -> grados = gradoService.findGradosByProfesorId(user.id());
                case "ROLE_TUTOR" -> grados = gradoService.findGradosByTutorId(user.id());
                default -> {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new ApiResponseDto<>(false, "No tiene permisos para ver los grados", null));
                }
            }

            return ResponseEntity.ok(new ApiResponseDto<>(true, "Grados encontrados", grados));
        } catch (Exception e) {
            throw new ApplicationException("Error al obtener grados: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un grado por Id")
    public ResponseEntity<ApiResponseDto<GradoDto>> getById(@PathVariable("id") Long id) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserResponseDTO user = userService.findByEmail(userDetails.getUsername());

            // Verificar permisos para ver el grado específico
            if (!securityService.canViewGrade(user.id(), id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponseDto<>(false, "No tiene permisos para ver este grado", null));
            }

            Optional<GradoDto> response = gradoService.findById(id);
            return response.map(gradoDto -> ResponseEntity.ok(new ApiResponseDto<>(true, "Grado encontrado", gradoDto))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(false, "Grado no encontrado", null)));
        } catch (Exception e) {
            throw new ApplicationException("Error al obtener el grado: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un grado por Id")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<ApiResponseDto<GradoDto>> deleteById(@PathVariable("id") Long id) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserResponseDTO user = userService.findByEmail(userDetails.getUsername());

            if (!user.rol().equals("ROLE_ADMINISTRADOR")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponseDto<>(false, "Solo los administradores pueden eliminar grados", null));
            }

            Optional<GradoDto> gradoToDelete = gradoService.findById(id);
            if (gradoToDelete.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponseDto<>(false, "Grado no encontrado", null));
            }

            // Verificar si el grado tiene estudiantes o profesores asignados
            if (gradoService.hasActiveAssociations(id)) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponseDto<>(false,
                                "No se puede eliminar el grado porque tiene estudiantes o profesores asignados", null));
            }

            gradoService.deleteById(id);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Grado eliminado exitosamente", gradoToDelete.get()));
        } catch (Exception e) {
            throw new ApplicationException("Error al eliminar el grado: " + e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Actualiza un grado")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<ApiResponseDto<GradoDto>> updateGrado(
            @PathVariable Long id,
            @RequestBody @Valid GradoDto gradoDto) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserResponseDTO user = userService.findByEmail(userDetails.getUsername());

            if (!user.rol().equals("ROLE_ADMINISTRADOR")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponseDto<>(false, "Solo los administradores pueden actualizar grados", null));
            }

            if (!gradoService.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponseDto<>(false, "Grado no encontrado", null));
            }

            gradoDto.setId(id); // Asegurar que el ID coincida
            GradoDto updatedGrado = gradoService.update(gradoDto);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Grado actualizado exitosamente", updatedGrado));
        } catch (Exception e) {
            throw new ApplicationException("Error al actualizar el grado: " + e.getMessage());
        }
    }
}
