package br.com.zelabairro.dto;

import java.time.LocalDate;

//Saida de uma ocorrencia (Documento Base 8.3). RN13: autor = so o primeiro nome
public class OcorrenciaResponseDto {

    private Integer id;
    private String descricao;
    private String endereco;
    private String status;
    private LocalDate dataAbertura;
    private LocalDate dataResolucao;
    private CategoriaResponseDto categoria;
    private BairroResponseDto bairro;
    private Integer idAutor;
    private String autor;
    private Integer prazoPrevistoDias;
    private String riscoAtraso;
    private Integer qtdApoios;
    private Integer qtdFotos;
    private Long diasEmAberto;
    private Boolean atrasada;
    private Integer pontuacaoPrioridade;
    private String prioridade;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDate dataAbertura) { this.dataAbertura = dataAbertura; }
    public LocalDate getDataResolucao() { return dataResolucao; }
    public void setDataResolucao(LocalDate dataResolucao) { this.dataResolucao = dataResolucao; }
    public CategoriaResponseDto getCategoria() { return categoria; }
    public void setCategoria(CategoriaResponseDto categoria) { this.categoria = categoria; }
    public BairroResponseDto getBairro() { return bairro; }
    public void setBairro(BairroResponseDto bairro) { this.bairro = bairro; }
    public Integer getIdAutor() { return idAutor; }
    public void setIdAutor(Integer idAutor) { this.idAutor = idAutor; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public Integer getPrazoPrevistoDias() { return prazoPrevistoDias; }
    public void setPrazoPrevistoDias(Integer prazoPrevistoDias) { this.prazoPrevistoDias = prazoPrevistoDias; }
    public String getRiscoAtraso() { return riscoAtraso; }
    public void setRiscoAtraso(String riscoAtraso) { this.riscoAtraso = riscoAtraso; }
    public Integer getQtdApoios() { return qtdApoios; }
    public void setQtdApoios(Integer qtdApoios) { this.qtdApoios = qtdApoios; }
    public Integer getQtdFotos() { return qtdFotos; }
    public void setQtdFotos(Integer qtdFotos) { this.qtdFotos = qtdFotos; }
    public Long getDiasEmAberto() { return diasEmAberto; }
    public void setDiasEmAberto(Long diasEmAberto) { this.diasEmAberto = diasEmAberto; }
    public Boolean getAtrasada() { return atrasada; }
    public void setAtrasada(Boolean atrasada) { this.atrasada = atrasada; }
    public Integer getPontuacaoPrioridade() { return pontuacaoPrioridade; }
    public void setPontuacaoPrioridade(Integer pontuacaoPrioridade) { this.pontuacaoPrioridade = pontuacaoPrioridade; }
    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }
}
