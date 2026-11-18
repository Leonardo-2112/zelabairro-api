package br.com.zelabairro.model;

import java.time.LocalDate;

import br.com.zelabairro.model.enums.StatusOcorrencia;

//tb_atualizacao: historico de status de uma ocorrencia (1:N com tb_ocorrencia)
public class Atualizacao {

    private int id;
    private int idOcorrencia;
    private StatusOcorrencia statusAnterior;
    private StatusOcorrencia statusNovo;
    private String comentario;
    private Gestor gestor;           //RN06: quem fez a atualizacao
    private LocalDate data;

    public Atualizacao() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdOcorrencia() { return idOcorrencia; }
    public void setIdOcorrencia(int idOcorrencia) { this.idOcorrencia = idOcorrencia; }
    public StatusOcorrencia getStatusAnterior() { return statusAnterior; }
    public void setStatusAnterior(StatusOcorrencia statusAnterior) { this.statusAnterior = statusAnterior; }
    public StatusOcorrencia getStatusNovo() { return statusNovo; }
    public void setStatusNovo(StatusOcorrencia statusNovo) { this.statusNovo = statusNovo; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public Gestor getGestor() { return gestor; }
    public void setGestor(Gestor gestor) { this.gestor = gestor; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
}
