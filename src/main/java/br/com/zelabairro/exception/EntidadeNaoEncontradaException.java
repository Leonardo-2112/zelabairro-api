package br.com.zelabairro.exception;

//404: o registro pedido (ou a FK informada) nao existe
public class EntidadeNaoEncontradaException extends RuntimeException {

    public EntidadeNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
