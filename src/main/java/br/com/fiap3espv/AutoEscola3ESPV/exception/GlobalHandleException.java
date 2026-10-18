package br.com.fiap3espv.AutoEscola3ESPV.exception;

import br.com.fiap3espv.AutoEscola3ESPV.exception.type.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import java.util.List;
@RestControllerAdvice
public class GlobalHandleException {
    @ExceptionHandler({EntityNotFoundException.class, AlunoNotFoundException.class, InstrutorNotFoundException.class})
    public ResponseEntity<ProblemDetail> naoEncontrado(RuntimeException ex) { return erro(404, ex.getMessage()); }
    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<ProblemDetail> negocio(ValidacaoException ex) { return erro(400, ex.getMessage()); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> campos(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Dados inválidos");
        problem.setProperty("erros", ex.getFieldErrors().stream().map(e -> new Campo(e.getField(), e.getDefaultMessage())).toList());
        return ResponseEntity.badRequest().body(problem);
    }
    @ExceptionHandler({HttpMessageNotReadableException.class, ConstraintViolationException.class, HandlerMethodValidationException.class})
    public ResponseEntity<ProblemDetail> formato(Exception ex) { return erro(400, "Requisição inválida. Confira os campos, tipos e o formato das datas."); }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> duplicidade(DataIntegrityViolationException ex) { return erro(409, "Cadastro duplicado ou dados incompatíveis com os limites do banco"); }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> autenticacao(AuthenticationException ex) { return erro(401, "Credenciais inválidas ou usuário inativo"); }
    @ExceptionHandler(ServicoExternoException.class)
    public ResponseEntity<ProblemDetail> externo(ServicoExternoException ex) { return erro(502, ex.getMessage()); }
    private ResponseEntity<ProblemDetail> erro(int status, String detalhe) {
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(HttpStatus.valueOf(status), detalhe));
    }
    private record Campo(String campo, String mensagem) {}
}
