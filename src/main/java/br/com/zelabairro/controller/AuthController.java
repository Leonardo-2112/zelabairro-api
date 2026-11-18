package br.com.zelabairro.controller;

import java.sql.SQLException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.zelabairro.bo.AuthBo;
import br.com.zelabairro.dto.LoginRequestDto;
import br.com.zelabairro.dto.LoginResponseDto;
import jakarta.validation.Valid;

//POST /auth/login: 200 {id, nome, perfil} | 400 campos invalidos | 401 e-mail ou senha errados (RN15)
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthBo authBo;

    public AuthController(AuthBo authBo) {
        this.authBo = authBo;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto dto) throws SQLException {
        return ResponseEntity.ok(authBo.login(dto.getEmail(), dto.getSenha()));
    }
}
