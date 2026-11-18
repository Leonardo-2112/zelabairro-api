package br.com.zelabairro.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

//Entrada do POST /auth/login (RN15)
public class LoginRequestDto {

    @NotBlank(message = "Informe o e-mail.")
    @Email(message = "E-mail inválido.")
    private String email;

    @NotBlank(message = "Informe a senha.")
    private String senha;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}
