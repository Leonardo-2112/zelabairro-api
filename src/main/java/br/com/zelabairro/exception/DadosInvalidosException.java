package br.com.zelabairro.exception;

//400: dado invalido que o Bean Validation nao consegue checar (ex.: tipo da foto, RN18)
public class DadosInvalidosException extends RuntimeException {

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
