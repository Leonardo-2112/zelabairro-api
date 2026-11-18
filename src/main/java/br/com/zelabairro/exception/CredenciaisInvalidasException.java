package br.com.zelabairro.exception;

//401: e-mail ou senha invalidos no login (RN15)
public class CredenciaisInvalidasException extends RuntimeException {

    public CredenciaisInvalidasException(String mensagem) {
        super(mensagem);
    }
}
