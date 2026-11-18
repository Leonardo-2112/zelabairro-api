package br.com.zelabairro.dto;

import java.time.LocalDate;

//Saida de GET /cidadaos/{id} (perfil). Nunca devolve a senha
public class CidadaoResponseDto {

    private Integer id;
    private String nome;
    private String email;
    private String telefone;
    private LocalDate dataCadastro;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }
}
