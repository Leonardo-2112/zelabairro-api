package br.com.zelabairro.dto;

import java.time.LocalDate;

//Apoio a uma ocorrencia (RN13: so o primeiro nome do cidadao)
public class ApoioResponseDto {

    private Integer idOcorrencia;
    private Integer idCidadao;
    private String cidadao;
    private LocalDate dataApoio;

    public Integer getIdOcorrencia() { return idOcorrencia; }
    public void setIdOcorrencia(Integer idOcorrencia) { this.idOcorrencia = idOcorrencia; }
    public Integer getIdCidadao() { return idCidadao; }
    public void setIdCidadao(Integer idCidadao) { this.idCidadao = idCidadao; }
    public String getCidadao() { return cidadao; }
    public void setCidadao(String cidadao) { this.cidadao = cidadao; }
    public LocalDate getDataApoio() { return dataApoio; }
    public void setDataApoio(LocalDate dataApoio) { this.dataApoio = dataApoio; }
}
