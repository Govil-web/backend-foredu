package com.dev.ForoEscolar.controllers.asistencia;


import com.dev.ForoEscolar.dtos.ApiResponseDto;
import com.dev.ForoEscolar.dtos.asistencia.AsistenciaDTO;
import com.dev.ForoEscolar.dtos.asistencia.AsistenciaRequestDto;
import com.dev.ForoEscolar.dtos.user.UserResponseDTO;
import com.dev.ForoEscolar.exceptions.ApplicationException;
import com.dev.ForoEscolar.services.AsistenciaService;
import com.dev.ForoEscolar.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public AsistenciaController(AsistenciaService asistenciaService, UserService userService) {
        this.asistenciaService = asistenciaService;
        this.userService = userService;
    }

    @PostMapping("/add")
    @Operation(summary = "Register a new asistencia")
    public ResponseEntity<ApiResponseDto<AsistenciaDTO>> registerAsistencia(@RequestBody AsistenciaRequestDto asistenciaDTO) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserResponseDTO user = userService.findByEmail(userDetails.getUsername());
        if (user.rol().equals("PROFESOR")) {
            asistenciaDTO.setProfesor(user.id());

            AsistenciaDTO asistencia = asistenciaService.save(asistenciaDTO);
            String message = "Asistencia saved";
            return ResponseEntity.ok(new ApiResponseDto<>(true, message, asistencia));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(false, "Usuario no autorizado", null));
        }

    }
    @GetMapping("/{id}")
    @Operation(summary = "Get asistencia by id")
    public ResponseEntity<ApiResponseDto<AsistenciaDTO>> getAsistenciaById(@PathVariable Long id) {
        return asistenciaService.findById(id)
                .map(asistencia -> ResponseEntity.ok(new ApiResponseDto<>(true, "Asistencia found", asistencia)))
                .orElseGet(() -> ResponseEntity.badRequest().body(new ApiResponseDto<>(false, "Asistencia not found", null)));

    }
    @GetMapping("/grado/{id}")
    @Operation(summary = "Get asistencia by grado")
    public ResponseEntity<ApiResponseDto<AsistenciaDTO>> getAsistenciaBygrado(@PathVariable Long id) {
        try {
            Iterable<AsistenciaDTO> listarAsistencias = asistenciaService.getAsistenciasByGrado(id);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Success", listarAsistencias));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/getAll")
    @Operation(summary = "List all asistencias")
    public ResponseEntity<ApiResponseDto<AsistenciaDTO>> asistenciasList() {

        try {
            Iterable<AsistenciaDTO> listarAsistencias = asistenciaService.findAll();
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Success", listarAsistencias));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(false, "An error occurred", null));
        }
    }
    @GetMapping("/fechaYgrado/{id}")
    @Operation(summary = "List all asistencias for a specific date and grade")
    public ResponseEntity<ApiResponseDto<AsistenciaDTO>> getAsistenciasByDateAndGrado(
            @PathVariable Long id,
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin)
    {
        try {
            Iterable<AsistenciaDTO> listarAsistencias = asistenciaService.getByFechaBeetweenAndGrado(id, fechaInicio, fechaFin);

            return ResponseEntity.ok(new ApiResponseDto<>(true, "Success", listarAsistencias));
        } catch (ApplicationException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(false, e.getMessage(), null));
        }

    }
    @PutMapping("/update/{id}")
    @Operation(summary = "Update asistencia")
    public ResponseEntity<ApiResponseDto<AsistenciaDTO>> updateAsistencia(@PathVariable Long id, @RequestBody AsistenciaRequestDto asistenciaDTO) {
        try {
             asistenciaService.update(asistenciaDTO);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Success Updated", null));
        } catch (ApplicationException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }

}
