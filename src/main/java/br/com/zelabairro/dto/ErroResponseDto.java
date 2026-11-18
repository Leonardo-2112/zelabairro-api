package br.com.zelabairro.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

//Formato padrao de erro (Documento Base 8.3): sempre JSON, nunca stack trace ou SQL
@JsonPropertyOrder({"status", "erro", "mensagem", "caminho", "timestamp", "campos"})
public class ErroResponseDto {

    private int status;
    private String erro;
    private String mensagem;
    private String caminho;
    private String timestamp;
    private List<CampoErroDto> campos;

    public ErroResponseDto(int status, String erro, String mensagem, String caminho, List<CampoErroDto> campos) {
        this.status = status;
        this.erro = erro;
        this.mensagem = mensagem;
        this.caminho = caminho;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        this.campos = campos != null ? campos : new ArrayList<>();
    }

    public int getStatus() { return status; }
    public String getErro() { return erro; }
    public String getMensagem() { return mensagem; }
    public String getCaminho() { return caminho; }
    public String getTimestamp() { return timestamp; }
    public List<CampoErroDto> getCampos() { return campos; }
}
