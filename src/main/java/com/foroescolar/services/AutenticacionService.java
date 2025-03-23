package com.foroescolar.services;

import com.foroescolar.dtos.jwttoken.JWTTokenDTO;
import com.foroescolar.dtos.user.DatosAutenticacionUsuario;
import com.foroescolar.model.User;

public interface AutenticacionService {

    JWTTokenDTO autenticar(DatosAutenticacionUsuario datosAutenticacionUsuario);


    User getCurrentUser(String name);
}
