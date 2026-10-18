package br.com.fiap3espv.AutoEscola3ESPV;

import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.Papel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(roles = "ADMIN")
class ConformidadeDocumentosTest extends BaseApiTest {
    @ParameterizedTest @ValueSource(strings = {"email", "cpf"})
    void rejeitaAlteracaoDosDadosImutaveisDoAluno(String campo) throws Exception {
        var aluno = aluno();
        mvc.perform(body(put("/aluno"), Map.of("id", aluno.getId(), campo,
            campo.equals("email") ? "outro@example.com" : "99999999999"))).andExpect(status().isBadRequest());
        var salvo = alunos.findById(aluno.getId()).orElseThrow();
        assertThat(salvo.getEmail()).isEqualTo(aluno.getEmail());
        assertThat(salvo.getCpf()).isEqualTo(aluno.getCpf());
    }

    @ParameterizedTest @ValueSource(strings = {"email", "cnh", "especialidade"})
    void rejeitaAlteracaoDosDadosImutaveisDoInstrutor(String campo) throws Exception {
        var instrutor = instrutor();
        String valor = campo.equals("email") ? "outro@example.com" : campo.equals("cnh") ? "99999999999" : "MOTOS";
        mvc.perform(body(put("/instrutor"), Map.of("id", instrutor.getId(), campo, valor))).andExpect(status().isBadRequest());
        var salvo = instrutores.findById(instrutor.getId()).orElseThrow();
        assertThat(salvo.getEmail()).isEqualTo(instrutor.getEmail());
        assertThat(salvo.getCnh()).isEqualTo(instrutor.getCnh());
        assertThat(salvo.getEspecialidade()).isEqualTo(instrutor.getEspecialidade());
    }

    @Test void listagemDeInstrutoresIncluiCnh() throws Exception {
        var instrutor = instrutor();
        mvc.perform(get("/instrutor")).andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].cnh").value(instrutor.getCnh()));
    }

    @Test void telefoneDoInstrutorPodeSerPreenchidoSomenteNaAtualizacao() throws Exception {
        var response = mvc.perform(body(post("/instrutor"), Map.of("nome", "Instrutor inicial", "email", "inicial@example.com",
            "cnh", "12345678901", "especialidade", "CARROS", "endereco", enderecoJson())))
            .andExpect(status().isCreated()).andReturn();
        long id = payload(response).get("id").asLong();
        assertThat(instrutores.findById(id).orElseThrow().getTelefone()).isNull();
        mvc.perform(body(put("/instrutor"), Map.of("id", id, "telefone", "11987654321")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.telefone").value("11987654321"));
        mvc.perform(get("/v3/api-docs"))
            .andExpect(jsonPath("$.components.schemas.DadosCadastroInstrutor.properties.telefone").doesNotExist());
    }

    @Test void aulasDeUmaHoraPodemComecarEmMeiaHoraSemSobreposicao() throws Exception {
        var instrutor = instrutor();
        mvc.perform(body(post("/instrucao"), agendamento(aluno(), instrutor, horario().plusMinutes(30)))).andExpect(status().isCreated());
        mvc.perform(body(post("/instrucao"), agendamento(aluno(), instrutor, horario()))).andExpect(status().isBadRequest());
        mvc.perform(body(post("/instrucao"), agendamento(aluno(), instrutor, horario().plusHours(1)))).andExpect(status().isBadRequest());
        mvc.perform(body(post("/instrucao"), agendamento(aluno(), instrutor, horario().plusHours(1).plusMinutes(30)))).andExpect(status().isCreated());
        mvc.perform(body(post("/instrucao"), agendamento(aluno(), instrutor, horario().minusMinutes(30)))).andExpect(status().isCreated());
    }

    @Test void exigeMotivoEmTodasAsRotasDeCancelamento() throws Exception {
        var aula = instrucao(aluno(), instrutor(), horario());
        mvc.perform(delete("/instrucao/{id}", aula.getId())).andExpect(status().isBadRequest());
        mvc.perform(body(post("/instrucao/{id}/cancelamento", aula.getId()), Map.of())).andExpect(status().isBadRequest());
        mvc.perform(delete("/instrucao/{id}", aula.getId()).param("motivo", "INVALIDO")).andExpect(status().isBadRequest());
        mvc.perform(get("/instrucao/{id}", aula.getId())).andExpect(jsonPath("$.status").value("AGENDADA"));
    }

    @Test void cancelamentoLiberaUmaDasDuasVagasDiarias() throws Exception {
        var aluno = aluno(); var instrutor = instrutor();
        var primeira = instrucao(aluno, instrutor, horario());
        instrucao(aluno, instrutor, horario().plusHours(1));
        mvc.perform(body(post("/instrucao"), agendamento(aluno, instrutor, horario().plusHours(2)))).andExpect(status().isBadRequest());
        mvc.perform(delete("/instrucao/{id}", primeira.getId()).param("motivo", "OUTROS")).andExpect(status().isNoContent());
        mvc.perform(body(post("/instrucao"), agendamento(aluno, instrutor, horario().plusHours(2)))).andExpect(status().isCreated());
    }

    private String autenticar(String login, String senha) throws Exception {
        var response = mvc.perform(body(post("/login"), Map.of("login", login, "senha", senha)))
            .andExpect(status().isOk()).andReturn();
        return payload(response).get("tokenJWT").asText();
    }

    @Test void usuarioComumAlteraAPropriaSenhaSemPoderAlterarOutroUsuario() throws Exception {
        var usuario = usuario(Papel.USER); var outro = usuario(Papel.USER);
        String hashDoOutro = outro.getSenha();
        String token = autenticar(usuario.getLogin(), "Senha@123");
        mvc.perform(body(put("/usuario/senha"), Map.of("senhaAtual", "Senha@123", "novaSenha", "NovaSenha@123", "id", outro.getId()))
            .header("Authorization", "Bearer " + token)).andExpect(status().isNoContent());
        assertThat(encoder.matches("NovaSenha@123", usuarios.findById(usuario.getId()).orElseThrow().getSenha())).isTrue();
        assertThat(usuarios.findById(outro.getId()).orElseThrow().getSenha()).isEqualTo(hashDoOutro);
        mvc.perform(body(post("/login"), Map.of("login", usuario.getLogin(), "senha", "Senha@123"))).andExpect(status().isUnauthorized());
        autenticar(usuario.getLogin(), "NovaSenha@123");
        mvc.perform(get("/usuario").header("Authorization", "Bearer " + token)).andExpect(status().isForbidden());
    }

    @Test void trocarSenhaExigeSenhaAtualCorreta() throws Exception {
        var usuario = usuario(Papel.USER);
        String token = autenticar(usuario.getLogin(), "Senha@123");
        mvc.perform(body(put("/usuario/senha"), Map.of("senhaAtual", "Incorreta", "novaSenha", "NovaSenha@123"))
            .header("Authorization", "Bearer " + token)).andExpect(status().isUnauthorized());
        assertThat(encoder.matches("Senha@123", usuarios.findById(usuario.getId()).orElseThrow().getSenha())).isTrue();
    }

    @Test @WithAnonymousUser void trocarSenhaExigeAutenticacao() throws Exception {
        mvc.perform(body(put("/usuario/senha"), Map.of("senhaAtual", "Senha@123", "novaSenha", "NovaSenha@123")))
            .andExpect(status().isUnauthorized());
    }
}
