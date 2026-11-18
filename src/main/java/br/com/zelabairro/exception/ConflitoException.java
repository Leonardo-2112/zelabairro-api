package br.com.zelabairro.exception;

//409: dado repetido (e-mail, nome) ou remocao de registro que tem filhos
public class ConflitoException extends RuntimeException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
