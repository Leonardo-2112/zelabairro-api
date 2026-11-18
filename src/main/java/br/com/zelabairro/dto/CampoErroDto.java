package br.com.zelabairro.dto;

//Um campo invalido dentro do erro 400: {"campo": "descricao", "mensagem": "..."}
public class CampoErroDto {

    private String campo;
    private String mensagem;

    public CampoErroDto(String campo, String mensagem) {
        this.campo = campo;
        this.mensagem = mensagem;
    }

    public String getCampo() { return campo; }
    public String getMensagem() { return mensagem; }
}
