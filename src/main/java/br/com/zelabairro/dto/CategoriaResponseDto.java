package br.com.zelabairro.dto;

//Saida de /categorias (tambem aparece dentro da ocorrencia)
public class CategoriaResponseDto {

    private Integer id;
    private String nome;
    private String descricao;
    private Integer prazoDias;
    private Integer gravidade;
    private Integer idOrgao;
    private String orgao;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Integer getPrazoDias() { return prazoDias; }
    public void setPrazoDias(Integer prazoDias) { this.prazoDias = prazoDias; }
    public Integer getGravidade() { return gravidade; }
    public void setGravidade(Integer gravidade) { this.gravidade = gravidade; }
    public Integer getIdOrgao() { return idOrgao; }
    public void setIdOrgao(Integer idOrgao) { this.idOrgao = idOrgao; }
    public String getOrgao() { return orgao; }
    public void setOrgao(String orgao) { this.orgao = orgao; }
}
