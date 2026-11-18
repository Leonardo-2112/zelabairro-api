package br.com.zelabairro.exception;

//409: regra de negocio violada (a mensagem comeca com o codigo RNxx)
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
