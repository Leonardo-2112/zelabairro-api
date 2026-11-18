package br.com.zelabairro.model;

//tb_bairro: bairro/distrito atendido
public class Bairro {

    private int id;
    private String nome;
    private String subprefeitura;
    private Integer populacao;

    public Bairro() {
    }

    public Bairro(int id) {
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getSubprefeitura() { return subprefeitura; }
    public void setSubprefeitura(String subprefeitura) { this.subprefeitura = subprefeitura; }
    public Integer getPopulacao() { return populacao; }
    public void setPopulacao(Integer populacao) { this.populacao = populacao; }
}
