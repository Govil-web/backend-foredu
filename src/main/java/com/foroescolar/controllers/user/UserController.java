package com.foroescolar.controllers.user;

import com.foroescolar.dtos.ApiResponseDto;
import com.foroescolar.dtos.user.UserRequestDTO;
import com.foroescolar.dtos.user.UserResponseDTO;
import com.foroescolar.exceptions.ApplicationException;
import com.foroescolar.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/user")
public class UserController {


    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Registra un nuevo usuario")
    public ResponseEntity<ApiResponseDto<UserResponseDTO>> registerUser(@RequestBody @Valid UserRequestDTO userRegisterDataDTO) {
        UserResponseDTO user = userService.save(userRegisterDataDTO);
        if (user == null) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(false, "Usuario no registrado", null));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDto<>(true, "Usuario registrado", user));
    }

    @GetMapping("/getAll")
    @Operation(summary = "Obtiene todos los usuarios")
    public ResponseEntity<ApiResponseDto<UserResponseDTO>> getAllUsers() {
        try {
            Iterable<UserResponseDTO> users = userService.findAll();
            return new ResponseEntity<>(new ApiResponseDto<>(true, "Usuarios encontrados", users), HttpStatus.CREATED);
        }catch (ApplicationException e){
            throw new ApplicationException( "Ha ocurrido un error ",e.getMessage() , e.getHttpStatus());
        }

    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un usuario por su ID")
    public ResponseEntity<ApiResponseDto<UserResponseDTO>> getUserById(@PathVariable Long id) {
        Optional<UserResponseDTO> user = userService.findById(id);
        if(user.isPresent()) {
            UserResponseDTO userResponseDTO = user.get();
            String message = "Usuario encontrado";
            return new ResponseEntity<>(new ApiResponseDto<>(true, message, userResponseDTO), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(new ApiResponseDto<>(false, "Usuario no encontrado", null), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update")
    @Operation(summary = "Actualiza un usuario")
    public ResponseEntity<ApiResponseDto<UserResponseDTO>> updateUser(@RequestBody @Valid UserRequestDTO userRegisterDataDTO) {
        try {
            UserResponseDTO user = userService.update(userRegisterDataDTO);
            return new ResponseEntity<>(new ApiResponseDto<>(true, "Usuario actualizado", user), HttpStatus.CREATED);
        } catch (ApplicationException e) {
            return new ResponseEntity<>(new ApiResponseDto<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

}
