package br.com.zelabairro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

//Entrada do POST e PUT /bairros
public class BairroRequestDto {

    @NotBlank(message = "Informe o nome do bairro.")
    @Size(max = 80, message = "O nome deve ter no máximo 80 caracteres.")
    private String nome;

    @NotBlank(message = "Informe a subprefeitura.")
    @Size(max = 80, message = "A subprefeitura deve ter no máximo 80 caracteres.")
    private String subprefeitura;

    @Positive(message = "A população deve ser maior que zero.")
    private Integer populacao;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getSubprefeitura() { return subprefeitura; }
    public void setSubprefeitura(String subprefeitura) { this.subprefeitura = subprefeitura; }
    public Integer getPopulacao() { return populacao; }
    public void setPopulacao(Integer populacao) { this.populacao = populacao; }
}
