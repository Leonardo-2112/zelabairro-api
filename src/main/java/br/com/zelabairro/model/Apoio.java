package br.com.zelabairro.model;

import java.time.LocalDate;

//tb_apoio: associativa do N:N cidadao <-> ocorrencia ("eu tambem tenho esse problema")
public class Apoio {

    private Cidadao cidadao;
    private int idOcorrencia;
    private LocalDate data;

    public Apoio() {
    }

    public Cidadao getCidadao() { return cidadao; }
    public void setCidadao(Cidadao cidadao) { this.cidadao = cidadao; }
    public int getIdOcorrencia() { return idOcorrencia; }
    public void setIdOcorrencia(int idOcorrencia) { this.idOcorrencia = idOcorrencia; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
}
