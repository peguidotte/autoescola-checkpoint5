package br.com.fiap3espv.AutoEscola3ESPV;

import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.*;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.*;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.endereco.Endereco;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.*;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test") @Transactional
abstract class BaseApiTest {
    @Autowired protected MockMvc mvc;
    @Autowired protected ObjectMapper json;
    @Autowired protected AlunoRepository alunos;
    @Autowired protected InstrutorRepository instrutores;
    @Autowired protected InstrucaoRepository instrucoes;
    @Autowired protected UsuarioRepository usuarios;
    @Autowired protected PasswordEncoder encoder;
    @Autowired protected Clock clock;
    private static final AtomicLong SEQUENCE = new AtomicLong(100);
    protected Endereco endereco() { return new Endereco("Praça da Sé", "10", "Sala 1", "Sé", "São Paulo", "SP", "01001000"); }
    protected Map<String, Object> enderecoJson() { return Map.of("logradouro", "Praça da Sé", "numero", "10", "bairro", "Sé", "cidade", "São Paulo", "uf", "SP", "cep", "01001000"); }
    protected Aluno aluno() {
        long n = SEQUENCE.incrementAndGet();
        return alunos.saveAndFlush(new Aluno(null, "Aluno " + n, "11999999999", "aluno" + n + "@teste.com", String.format("%011d", n), endereco(), true));
    }
    protected Instrutor instrutor() {
        long n = SEQUENCE.incrementAndGet();
        return instrutores.saveAndFlush(new Instrutor(null, "Instrutor " + n, "11999999999", "instrutor" + n + "@teste.com", String.format("%011d", n), Especialidade.CARROS, endereco(), true));
    }
    protected Usuario usuario(Papel papel) {
        long n = SEQUENCE.incrementAndGet();
        return usuarios.saveAndFlush(new Usuario(null, "usuario" + n, encoder.encode("Senha@123"), papel, true));
    }
    protected LocalDateTime horario() {
        return LocalDateTime.now(clock).with(TemporalAdjusters.next(DayOfWeek.MONDAY)).plusWeeks(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
    }
    protected Instrucao instrucao(Aluno aluno, Instrutor instrutor, LocalDateTime horario) {
        return instrucoes.saveAndFlush(new Instrucao(null, aluno, instrutor, horario));
    }
    protected Map<String, Object> agendamento(Aluno aluno, Instrutor instrutor, LocalDateTime data) {
        return Map.of("idAluno", aluno.getId(), "idInstrutor", instrutor.getId(), "dataHora", data.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm")));
    }
    protected MockHttpServletRequestBuilder body(MockHttpServletRequestBuilder builder, Object value) {
        return builder.contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(value));
    }
    protected JsonNode payload(MvcResult result) throws Exception { return json.readTree(result.getResponse().getContentAsString()); }
}
