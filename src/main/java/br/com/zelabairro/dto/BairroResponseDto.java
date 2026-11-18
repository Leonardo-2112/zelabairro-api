package br.com.zelabairro.dto;

//Saida de /bairros (tambem aparece dentro da ocorrencia)
public class BairroResponseDto {

    private Integer id;
    private String nome;
    private String subprefeitura;
    private Integer populacao;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getSubprefeitura() { return subprefeitura; }
    public void setSubprefeitura(String subprefeitura) { this.subprefeitura = subprefeitura; }
    public Integer getPopulacao() { return populacao; }
    public void setPopulacao(Integer populacao) { this.populacao = populacao; }
}
