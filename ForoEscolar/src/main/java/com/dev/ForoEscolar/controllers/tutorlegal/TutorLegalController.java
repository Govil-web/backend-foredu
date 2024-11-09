package com.dev.ForoEscolar.controllers.tutorlegal;

import com.dev.ForoEscolar.dtos.ApiResponseDto;
import com.dev.ForoEscolar.dtos.asistencia.AsistenciaDTO;
import com.dev.ForoEscolar.dtos.tutorlegal.TutorLegalRequestDTO;
import com.dev.ForoEscolar.dtos.tutorlegal.TutorLegalResponseDTO;
import com.dev.ForoEscolar.dtos.user.UserResponseDTO;
import com.dev.ForoEscolar.exceptions.ApplicationException;
import com.dev.ForoEscolar.services.TutorLegalService;
import com.dev.ForoEscolar.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("api/tutorlegal")
public class TutorLegalController {

    private final TutorLegalService tutorLegalService;
    private final UserService userService;

    @Autowired
    public TutorLegalController(TutorLegalService tutorLegalService, UserService userService) {
        this.tutorLegalService = tutorLegalService;
        this.userService = userService;
    }

    @GetMapping("/getAll")
    @Operation(summary = "se obtiene toda la lista de tutores legales")
    public ResponseEntity<Iterable<TutorLegalResponseDTO>> getAll() {
        try{
            Iterable<TutorLegalResponseDTO> list = tutorLegalService.findAll();
            return new ResponseEntity<>(list, HttpStatus.OK);
        }catch (ApplicationException e){
            throw new ApplicationException(" Ocurrió un error al retornar la lista " +e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "se obtiene un tutor legal por su id")
    public ResponseEntity<TutorLegalResponseDTO> getById(@PathVariable Long id) {
        Optional<TutorLegalResponseDTO> tutor = tutorLegalService.findById(id);
        return tutor.map(tutorLegalResponseDTO -> new ResponseEntity<>(tutorLegalResponseDTO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/asistenciaHijo/{idGrado}")
    @Operation(summary = "Obtiene la lista de asistencias del hijo por el grado correspondiente")
    public ResponseEntity<ApiResponseDto<AsistenciaDTO>> getAsistenciaHijo(@PathVariable Long idGrado) {
        UserDetails userDetails= (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserResponseDTO user= userService.findByEmail(userDetails.getUsername());
        try {
            if(user.rol().equals("TUTOR")){
                Iterable<AsistenciaDTO> asistencia = tutorLegalService.findAsistenciasByEstudianteId(user.id(), idGrado);
                return new ResponseEntity<>(new ApiResponseDto<>(true,"Exito", asistencia),HttpStatus.OK);
            }
            return new ResponseEntity<>(new ApiResponseDto<>(false, "Usuario no autorizado", null),HttpStatus.UNAUTHORIZED);
        } catch (ApplicationException e) {
            throw new ApplicationException("Ha ocurrido un error " + e.getMessage());
        }
    }


    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Registra un nuevo tutor legal")
    public ResponseEntity<ApiResponseDto<TutorLegalResponseDTO>> registerTutorLegal(@RequestBody @Valid TutorLegalRequestDTO tutorLegalRequestDTO) {
        TutorLegalResponseDTO tutorLegal = tutorLegalService.save(tutorLegalRequestDTO);
        if (tutorLegal == null) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(false, "Tutor legal no registrado", null));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDto<>(true, "Tutor legal registrado", tutorLegal));
    }

    @PutMapping("/update")
    @Operation(summary = "actualiza un tutor legal específico")
    public ResponseEntity<ApiResponseDto<TutorLegalResponseDTO>> update(@RequestBody TutorLegalRequestDTO tutorLegalRequestDTO) {

      try   {
          TutorLegalResponseDTO Tutor = tutorLegalService.update(tutorLegalRequestDTO);
          return new ResponseEntity<>(new ApiResponseDto<>(true, "Tutor Actualizado", Tutor), HttpStatus.CREATED);
      } catch (ApplicationException e) {
         return new ResponseEntity<>(new ApiResponseDto<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
      }
      }

    @DeleteMapping("/{id}")
    @Operation(summary = "elimina un tutor legal dando como parametro el id correspondiente")
    public ResponseEntity<TutorLegalResponseDTO> delete(@PathVariable Long id) {
        tutorLegalService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
