package br.com.zelabairro.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

//Entrada do POST e PUT /cidadaos (cadastro publico, sempre cria cidadao - RN17)
public class CidadaoRequestDto {

    @NotBlank(message = "Informe o nome.")
    @Size(min = 3, max = 100, message = "O nome deve ter de 3 a 100 caracteres.")
    private String nome;

    @NotBlank(message = "Informe o e-mail.")
    @Email(message = "E-mail inválido.")
    @Size(max = 120, message = "O e-mail deve ter no máximo 120 caracteres.")
    private String email;

    @NotBlank(message = "Informe a senha.")
    @Size(min = 6, max = 60, message = "RN15: a senha deve ter de 6 a 60 caracteres.")
    private String senha;

    @Pattern(regexp = "^[0-9()+\\- ]{8,20}$", message = "Telefone inválido (use só números, espaço, parênteses e hífen).")
    private String telefone;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}
