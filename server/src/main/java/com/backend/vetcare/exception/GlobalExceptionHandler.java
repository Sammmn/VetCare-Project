package com.backend.vetcare.exception;

import com.backend.vetcare.dto.response.ErroResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErroResponse> handleApi(ApiException ex, HttpServletRequest req) {
        return build(ex.getStatus().value(), ex.getMessage(), req, Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidacao(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> campos.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return build(400, "Dados inválidos. Verifique os campos informados.", req, campos);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> handleCorpoInvalido(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return build(400, "Corpo da requisição ausente, malformado ou com valores inválidos.", req, Map.of());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> handleTipoParametro(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        return build(400, "Valor inválido para o parâmetro '" + ex.getName() + "'.", req, Map.of());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResponse> handleAcessoNegado(AccessDeniedException ex, HttpServletRequest req) {
        return build(403, "Você não tem permissão para acessar este recurso.", req, Map.of());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErroResponse> handleAutenticacao(AuthenticationException ex, HttpServletRequest req) {
        return build(401, "Não autenticado.", req, Map.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> handleIntegridade(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.warn("Violação de integridade: {}", ex.getMostSpecificCause().getMessage());
        return build(409, "A operação viola uma restrição de integridade dos dados.", req, Map.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleGenerico(Exception ex, HttpServletRequest req) {
        // Erros padrão do Spring MVC (404 de rota, 405, parâmetro ausente...) já trazem o status correto.
        if (ex instanceof ErrorResponse er) {
            int status = er.getStatusCode().value();
            String detalhe = er.getBody().getDetail();
            return build(status, detalhe != null ? detalhe : "Requisição inválida.", req, Map.of());
        }
        log.error("Erro inesperado em {} {}", req.getMethod(), req.getRequestURI(), ex);
        return build(500, "Erro interno do servidor.", req, Map.of());
    }

    private ResponseEntity<ErroResponse> build(int status, String mensagem, HttpServletRequest req, Map<String, String> campos) {
        HttpStatus hs = HttpStatus.resolve(status);
        String erro = hs != null ? hs.getReasonPhrase() : "Erro";
        ErroResponse body = new ErroResponse(Instant.now().toString(), status, erro, mensagem, req.getRequestURI(), campos);
        return ResponseEntity.status(status).body(body);
    }
}
