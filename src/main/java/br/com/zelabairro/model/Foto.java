package br.com.zelabairro.model;

import java.time.LocalDate;

//tb_foto: foto do problema, guardada como BLOB no Oracle (RN18)
public class Foto {

    private int id;
    private int idOcorrencia;
    private byte[] imagem;   //bytes do arquivo (so carregado em GET /fotos/{id})
    private String tipo;     //image/jpeg ou image/png
    private long tamanho;    //em bytes
    private LocalDate dataEnvio;

    public Foto() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdOcorrencia() { return idOcorrencia; }
    public void setIdOcorrencia(int idOcorrencia) { this.idOcorrencia = idOcorrencia; }
    public byte[] getImagem() { return imagem; }
    public void setImagem(byte[] imagem) { this.imagem = imagem; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public long getTamanho() { return tamanho; }
    public void setTamanho(long tamanho) { this.tamanho = tamanho; }
    public LocalDate getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(LocalDate dataEnvio) { this.dataEnvio = dataEnvio; }
}
