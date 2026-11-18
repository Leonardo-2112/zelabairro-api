package br.com.zelabairro.model;

//tb_categoria: tipo de problema, com prazo de referencia e gravidade
public class Categoria {

    private int id;
    private String nome;
    private String descricao;
    private int prazoDias;   //prazo de referencia (RN11)
    private int gravidade;   //1 a 5 (peso da RN10)
    private Orgao orgao;

    public Categoria() {
    }

    public Categoria(int id) {
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public int getPrazoDias() { return prazoDias; }
    public void setPrazoDias(int prazoDias) { this.prazoDias = prazoDias; }
    public int getGravidade() { return gravidade; }
    public void setGravidade(int gravidade) { this.gravidade = gravidade; }
    public Orgao getOrgao() { return orgao; }
    public void setOrgao(Orgao orgao) { this.orgao = orgao; }
}
