package br.com.zelabairro.handler;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import br.com.zelabairro.dto.CampoErroDto;
import br.com.zelabairro.dto.ErroResponseDto;
import br.com.zelabairro.exception.AcessoNegadoException;
import br.com.zelabairro.exception.ConflitoException;
import br.com.zelabairro.exception.CredenciaisInvalidasException;
import br.com.zelabairro.exception.DadosInvalidosException;
import br.com.zelabairro.exception.EntidadeNaoEncontradaException;
import br.com.zelabairro.exception.RegraNegocioException;
import jakarta.servlet.http.HttpServletRequest;

//Transforma toda excecao no JSON padrao {status, erro, mensagem, caminho, timestamp, campos}
//Nunca devolve stack trace nem SQL: o detalhe tecnico fica so no log do servidor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    //Monta a resposta padrao
    private ResponseEntity<ErroResponseDto> resposta(HttpStatus status, String erro, String mensagem,
                                                     HttpServletRequest request, List<CampoErroDto> campos) {
        ErroResponseDto corpo = new ErroResponseDto(status.value(), erro, mensagem, request.getRequestURI(), campos);
        return ResponseEntity.status(status).body(corpo);
    }

    // ---------- Excecoes proprias do dominio ----------

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<ErroResponseDto> naoEncontrado(EntidadeNaoEncontradaException e, HttpServletRequest request) {
        return resposta(HttpStatus.NOT_FOUND, "Não encontrado", e.getMessage(), request, null);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponseDto> regraNegocio(RegraNegocioException e, HttpServletRequest request) {
        return resposta(HttpStatus.CONFLICT, "Regra de negócio violada", e.getMessage(), request, null);
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErroResponseDto> conflito(ConflitoException e, HttpServletRequest request) {
        return resposta(HttpStatus.CONFLICT, "Conflito", e.getMessage(), request, null);
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ErroResponseDto> naoAutorizado(CredenciaisInvalidasException e, HttpServletRequest request) {
        return resposta(HttpStatus.UNAUTHORIZED, "Não autorizado", e.getMessage(), request, null);
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<ErroResponseDto> acessoNegado(AcessoNegadoException e, HttpServletRequest request) {
        return resposta(HttpStatus.FORBIDDEN, "Acesso negado", e.getMessage(), request, null);
    }

    @ExceptionHandler(DadosInvalidosException.class)
    public ResponseEntity<ErroResponseDto> dadosInvalidos(DadosInvalidosException e, HttpServletRequest request) {
        return resposta(HttpStatus.BAD_REQUEST, "Requisição inválida", e.getMessage(), request, null);
    }

    // ---------- 400: Bean Validation (@Valid) com a lista de campos ----------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponseDto> validacao(MethodArgumentNotValidException e, HttpServletRequest request) {
        List<CampoErroDto> campos = e.getFieldErrors().stream()
                .map(campo -> new CampoErroDto(campo.getField(), campo.getDefaultMessage()))
                .toList();
        return resposta(HttpStatus.BAD_REQUEST, "Requisição inválida", "Um ou mais campos estão inválidos.", request, campos);
    }

    // ---------- 400: corpo, parametro ou arquivo mal formados ----------

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponseDto> jsonInvalido(HttpMessageNotReadableException e, HttpServletRequest request) {
        return resposta(HttpStatus.BAD_REQUEST, "Requisição inválida", "Corpo da requisição ausente ou com JSON inválido.", request, null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponseDto> parametroInvalido(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        return resposta(HttpStatus.BAD_REQUEST, "Requisição inválida", "Parâmetro '" + e.getName() + "' com valor inválido.", request, null);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErroResponseDto> arquivoGrande(MaxUploadSizeExceededException e, HttpServletRequest request) {
        return resposta(HttpStatus.BAD_REQUEST, "Requisição inválida", "RN18: a foto deve ter no máximo 2 MB.", request, null);
    }

    @ExceptionHandler({MissingServletRequestPartException.class, MultipartException.class})
    public ResponseEntity<ErroResponseDto> semArquivo(Exception e, HttpServletRequest request) {
        return resposta(HttpStatus.BAD_REQUEST, "Requisição inválida", "Envie a foto no campo 'arquivo' (multipart/form-data).", request, null);
    }

    // ---------- 404, 405 e 415 do proprio Spring ----------

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResponseDto> rotaInexistente(NoResourceFoundException e, HttpServletRequest request) {
        return resposta(HttpStatus.NOT_FOUND, "Não encontrado", "Rota não encontrada.", request, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResponseDto> metodoNaoSuportado(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        return resposta(HttpStatus.METHOD_NOT_ALLOWED, "Método não permitido",
                "O método " + e.getMethod() + " não é suportado nesta rota.", request, null);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErroResponseDto> tipoNaoSuportado(HttpMediaTypeNotSupportedException e, HttpServletRequest request) {
        return resposta(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Tipo de conteúdo não suportado",
                "Envie o corpo como application/json.", request, null);
    }

    // ---------- Banco e erros inesperados: mensagem generica ----------

    //Restricao do banco violada (UNIQUE, FK ou CHECK) que escapou das validacoes da API
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ErroResponseDto> integridade(SQLIntegrityConstraintViolationException e, HttpServletRequest request) {
        log.warn("Restricao do banco violada em {}: {}", request.getRequestURI(), e.getMessage());
        return resposta(HttpStatus.CONFLICT, "Conflito", "A operação viola uma restrição dos dados.", request, null);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErroResponseDto> erroBanco(SQLException e, HttpServletRequest request) {
        log.error("Erro de banco em {}", request.getRequestURI(), e);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno",
                "Não foi possível concluir a operação. Tente novamente mais tarde.", request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponseDto> erroGeral(Exception e, HttpServletRequest request) {
        log.error("Erro inesperado em {}", request.getRequestURI(), e);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno",
                "Não foi possível concluir a operação. Tente novamente mais tarde.", request, null);
    }
}
