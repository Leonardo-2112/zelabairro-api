package br.com.zelabairro.dto;

import java.time.LocalDate;

//Saida de /gestores. Nunca devolve a senha
public class GestorResponseDto {

    private Integer id;
    private String nome;
    private String email;
    private Integer idOrgao;
    private String orgao;
    private Integer idGestorCadastro;
    private LocalDate dataCadastro;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getIdOrgao() { return idOrgao; }
    public void setIdOrgao(Integer idOrgao) { this.idOrgao = idOrgao; }
    public String getOrgao() { return orgao; }
    public void setOrgao(String orgao) { this.orgao = orgao; }
    public Integer getIdGestorCadastro() { return idGestorCadastro; }
    public void setIdGestorCadastro(Integer idGestorCadastro) { this.idGestorCadastro = idGestorCadastro; }
    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }
}
