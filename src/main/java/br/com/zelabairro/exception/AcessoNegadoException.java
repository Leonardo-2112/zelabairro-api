package br.com.zelabairro.exception;

//403: rota so de gestor (RN16) ou acao so do autor (RN18)
public class AcessoNegadoException extends RuntimeException {

    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
