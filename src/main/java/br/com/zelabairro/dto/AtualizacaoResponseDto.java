package br.com.zelabairro.dto;

import java.time.LocalDate;

//Item do historico de status de uma ocorrencia
public class AtualizacaoResponseDto {

    private Integer id;
    private Integer idOcorrencia;
    private String statusAnterior;
    private String statusNovo;
    private String comentario;
    private LocalDate data;
    private Integer idGestor;
    private String gestor;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getIdOcorrencia() { return idOcorrencia; }
    public void setIdOcorrencia(Integer idOcorrencia) { this.idOcorrencia = idOcorrencia; }
    public String getStatusAnterior() { return statusAnterior; }
    public void setStatusAnterior(String statusAnterior) { this.statusAnterior = statusAnterior; }
    public String getStatusNovo() { return statusNovo; }
    public void setStatusNovo(String statusNovo) { this.statusNovo = statusNovo; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public Integer getIdGestor() { return idGestor; }
    public void setIdGestor(Integer idGestor) { this.idGestor = idGestor; }
    public String getGestor() { return gestor; }
    public void setGestor(String gestor) { this.gestor = gestor; }
}
