package br.com.zelabairro.dto;

import java.time.LocalDate;

//Item de GET /ocorrencias/{id}/fotos (sem os bytes; a imagem vem de GET /fotos/{id})
public class FotoResponseDto {

    private Integer id;
    private String tipo;
    private Long tamanho;
    private LocalDate dataEnvio;
    private String url;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Long getTamanho() { return tamanho; }
    public void setTamanho(Long tamanho) { this.tamanho = tamanho; }
    public LocalDate getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(LocalDate dataEnvio) { this.dataEnvio = dataEnvio; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
