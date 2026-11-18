package br.com.zelabairro.model;

import java.time.LocalDate;

//tb_cidadao: quem registra e apoia ocorrencias
public class Cidadao {

    private int id;
    private String nome;
    private String email;
    private String senha; //sempre o hash BCrypt (RN15)
    private String telefone;
    private LocalDate dataCadastro;

    public Cidadao() {
    }

    public Cidadao(int id) {
        this.id = id;
    }

    //RN13: listagens publicas mostram so o primeiro nome
    public String getPrimeiroNome() {
        if (nome == null) return null;
        return nome.trim().split("\\s+")[0];
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }
}
