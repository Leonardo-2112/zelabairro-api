package br.com.zelabairro.dto;

//Saida da listagem publica GET /cidadaos (RN13: so o primeiro nome, sem e-mail/telefone)
public class CidadaoResumoDto {

    private Integer id;
    private String nome;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
