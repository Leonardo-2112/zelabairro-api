package br.com.zelabairro.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

//Entrada do POST e PUT /gestores. Quem cadastra vem do cabecalho X-Gestor-Id (RN17)
public class GestorRequestDto {

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

    @NotNull(message = "Informe o órgão.")
    @Positive(message = "idOrgao deve ser positivo.")
    private Integer idOrgao;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public Integer getIdOrgao() { return idOrgao; }
    public void setIdOrgao(Integer idOrgao) { this.idOrgao = idOrgao; }
}
