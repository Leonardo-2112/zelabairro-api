package br.com.zelabairro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

//Entrada do POST /ocorrencias (RN01 e RN03)
public class OcorrenciaRequestDto {

    @NotBlank(message = "Informe a descrição.")
    @Size(min = 10, max = 500, message = "RN03: a descrição deve ter de 10 a 500 caracteres.")
    private String descricao;

    @NotBlank(message = "Informe o endereço de referência.")
    @Size(max = 200, message = "RN03: o endereço deve ter no máximo 200 caracteres.")
    private String endereco;

    @NotNull(message = "Informe o cidadão autor.")
    @Positive(message = "idCidadao deve ser positivo.")
    private Integer idCidadao;

    @NotNull(message = "Informe a categoria.")
    @Positive(message = "idCategoria deve ser positivo.")
    private Integer idCategoria;

    @NotNull(message = "Informe o bairro.")
    @Positive(message = "idBairro deve ser positivo.")
    private Integer idBairro;

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public Integer getIdCidadao() { return idCidadao; }
    public void setIdCidadao(Integer idCidadao) { this.idCidadao = idCidadao; }
    public Integer getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Integer idCategoria) { this.idCategoria = idCategoria; }
    public Integer getIdBairro() { return idBairro; }
    public void setIdBairro(Integer idBairro) { this.idBairro = idBairro; }
}
