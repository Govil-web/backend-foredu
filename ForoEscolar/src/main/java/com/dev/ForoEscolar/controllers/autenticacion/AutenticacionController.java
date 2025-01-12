package com.dev.ForoEscolar.controllers.autenticacion;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.dev.ForoEscolar.dtos.jwttoken.JWTTokenDTO;
import com.dev.ForoEscolar.dtos.user.DatosAutenticacionUsuario;
import com.dev.ForoEscolar.services.AutenticacionService;
import com.dev.ForoEscolar.services.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
public class AutenticacionController {

    private final TokenService tokenService;


    private final AutenticacionService autenticacionService;
    @Autowired
    public AutenticacionController(AutenticacionService autenticacionService, TokenService tokenService) {
        this.autenticacionService = autenticacionService;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<JWTTokenDTO> autenticar(@RequestBody @Valid DatosAutenticacionUsuario datosAutenticacionUsuario){
        JWTTokenDTO jwtTokenDTO = autenticacionService.autenticar(datosAutenticacionUsuario);
        return ResponseEntity.ok(jwtTokenDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader("Authorization") String authHeader) {

        Map<String, String> response = new HashMap<>();

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.put("mensaje", "Token no proporcionado");
                return ResponseEntity.badRequest().body(response);
            }

            String token = authHeader.substring(7).trim();
            tokenService.invalidateToken(token);

            response.put("mensaje", "Sesión cerrada exitosamente");
            return ResponseEntity.ok(response);

        } catch (TokenExpiredException e) {
            response.put("mensaje", "El token ya estaba expirado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

        } catch (Exception e) {
            response.put("mensaje", "Error al cerrar sesión");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
