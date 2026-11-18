package br.com.zelabairro.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

//Entrada do POST /ocorrencias/{id}/apoios
public class ApoioRequestDto {

    @NotNull(message = "Informe o cidadão que apoia.")
    @Positive(message = "idCidadao deve ser positivo.")
    private Integer idCidadao;

    public Integer getIdCidadao() { return idCidadao; }
    public void setIdCidadao(Integer idCidadao) { this.idCidadao = idCidadao; }
}
