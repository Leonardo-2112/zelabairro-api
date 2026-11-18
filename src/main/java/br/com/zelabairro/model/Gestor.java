package br.com.zelabairro.model;

import java.time.LocalDate;

//tb_gestor: servidor da prefeitura que usa o painel (RN16, RN17)
public class Gestor {

    private int id;
    private String nome;
    private String email;
    private String senha; //sempre o hash BCrypt (RN15)
    private Orgao orgao;
    private Integer idGestorCadastro; //quem cadastrou (nulo so no gestor do seed)
    private LocalDate dataCadastro;

    public Gestor() {
    }

    public Gestor(int id) {
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public Orgao getOrgao() { return orgao; }
    public void setOrgao(Orgao orgao) { this.orgao = orgao; }
    public Integer getIdGestorCadastro() { return idGestorCadastro; }
    public void setIdGestorCadastro(Integer idGestorCadastro) { this.idGestorCadastro = idGestorCadastro; }
    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }
}
