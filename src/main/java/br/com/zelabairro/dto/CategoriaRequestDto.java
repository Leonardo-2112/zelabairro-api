package br.com.zelabairro.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

//Entrada do POST e PUT /categorias
public class CategoriaRequestDto {

    @NotBlank(message = "Informe o nome da categoria.")
    @Size(max = 60, message = "O nome deve ter no máximo 60 caracteres.")
    private String nome;

    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres.")
    private String descricao;

    @NotNull(message = "Informe o prazo de referência em dias.")
    @Positive(message = "O prazo deve ser maior que zero.")
    @Max(value = 999, message = "O prazo deve ter no máximo 999 dias.")
    private Integer prazoDias;

    @NotNull(message = "Informe a gravidade.")
    @Min(value = 1, message = "A gravidade vai de 1 a 5.")
    @Max(value = 5, message = "A gravidade vai de 1 a 5.")
    private Integer gravidade;

    @NotNull(message = "Informe o órgão responsável.")
    @Positive(message = "idOrgao deve ser positivo.")
    private Integer idOrgao;

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
}
