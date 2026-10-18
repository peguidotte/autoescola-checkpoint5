package br.com.fiap3espv.AutoEscola3ESPV;

import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.MotivoCancelamento;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import java.time.*;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(roles = "USER")
class InstrucaoApiTest extends BaseApiTest {
    @Test void crudCompletoAgendamentoReagendamentoECancelamento() throws Exception {
        var aluno = aluno(); var instrutor = instrutor();
        var result = mvc.perform(body(post("/instrucao"), agendamento(aluno, instrutor, horario())))
            .andExpect(status().isCreated()).andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.status").value("AGENDADA")).andReturn();
        long id = payload(result).get("id").asLong();
        mvc.perform(get("/instrucao/{id}", id)).andExpect(status().isOk()).andExpect(jsonPath("$.idAluno").value(aluno.getId()));
        mvc.perform(get("/instrucao")).andExpect(jsonPath("$.totalElements").value(1));
        var update = new java.util.HashMap<>(agendamento(aluno, instrutor, horario().plusHours(1))); update.put("id", id);
        mvc.perform(body(put("/instrucao"), update)).andExpect(status().isOk()).andExpect(jsonPath("$.data_hora").value(horario().plusHours(1).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm"))));
        mvc.perform(delete("/instrucao/{id}", id).param("motivo", "ALUNO_DESISTIU")).andExpect(status().isNoContent());
        mvc.perform(get("/instrucao/{id}", id)).andExpect(jsonPath("$.status").value("CANCELADA")).andExpect(jsonPath("$.motivoCancelamento").value("ALUNO_DESISTIU"));
        assertThat(instrucoes.findById(id)).isPresent();
    }
    @Test void sorteiaInstrutorAtivoDisponivel() throws Exception {
        var ocupado = instrutor(); var livre = instrutor();
        instrucao(aluno(), ocupado, horario());
        mvc.perform(body(post("/instrucao"), Map.of("id_aluno", aluno().getId(), "especialidade", "CARROS", "data_hora", horario().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm")))))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.idInstrutor").value(livre.getId()));
    }
    @Test void impedeConflitoDoInstrutorMesmoQuandoIdsDaInstrucaoDiferem() throws Exception {
        var i = instrutor(); instrucao(aluno(), i, horario());
        mvc.perform(body(post("/instrucao"), agendamento(aluno(), i, horario()))).andExpect(status().isBadRequest()).andExpect(jsonPath("$.detail").value("Instrutor ocupado na data/hora informada"));
    }
    @Test void impedeDuasAulasDoAlunoNoMesmoDia() throws Exception {
        var a = aluno(); instrucao(a, instrutor(), horario());
        mvc.perform(body(post("/instrucao"), agendamento(a, instrutor(), horario().plusHours(1)))).andExpect(status().isBadRequest()).andExpect(jsonPath("$.detail").value("Permitida apenas uma instrução diária por aluno"));
    }
    @Test void rejeitaAlunoInativo() throws Exception {
        var a = aluno(); a.excluir(); alunos.flush();
        mvc.perform(body(post("/instrucao"), agendamento(a, instrutor(), horario()))).andExpect(status().isBadRequest());
    }
    @Test void rejeitaInstrutorInativo() throws Exception {
        var i = instrutor(); i.excluir(); instrutores.flush();
        mvc.perform(body(post("/instrucao"), agendamento(aluno(), i, horario()))).andExpect(status().isBadRequest());
    }
    @Test void rejeitaEspecialidadeDivergente() throws Exception {
        var data = new java.util.HashMap<>(agendamento(aluno(), instrutor(), horario())); data.put("especialidade", "MOTOS");
        mvc.perform(body(post("/instrucao"), data)).andExpect(status().isBadRequest());
    }
    @Test void exigeEspecialidadeSemInstrutor() throws Exception {
        var data = new java.util.HashMap<>(agendamento(aluno(), instrutor(), horario())); data.remove("idInstrutor");
        mvc.perform(body(post("/instrucao"), data)).andExpect(status().isBadRequest());
    }
    @Test void falhaQuandoNaoHaInstrutoresDaEspecialidade() throws Exception {
        var data = new java.util.HashMap<>(agendamento(aluno(), instrutor(), horario())); data.remove("idInstrutor"); data.put("especialidade", "VANS");
        mvc.perform(body(post("/instrucao"), data)).andExpect(status().isBadRequest());
    }
    @Test void validaJsonCamposObrigatoriosEDataPassada() throws Exception {
        mvc.perform(post("/instrucao")).andExpect(status().isBadRequest());
        mvc.perform(body(post("/instrucao"), Map.of())).andExpect(status().isBadRequest());
        mvc.perform(body(post("/instrucao"), agendamento(aluno(), instrutor(), horario().minusYears(10)))).andExpect(status().isBadRequest());
        mvc.perform(post("/instrucao").contentType("application/json").content("{quebrado")).andExpect(status().isBadRequest());
    }
    @Test void idsInexistentesRetornam404() throws Exception {
        var data = new java.util.HashMap<>(agendamento(aluno(), instrutor(), horario())); data.put("idAluno", 999999);
        mvc.perform(body(post("/instrucao"), data)).andExpect(status().isNotFound());
        data.put("idAluno", aluno().getId()); data.put("idInstrutor", 999999);
        mvc.perform(body(post("/instrucao"), data)).andExpect(status().isNotFound());
        mvc.perform(get("/instrucao/999999")).andExpect(status().isNotFound());
        mvc.perform(delete("/instrucao/999999")).andExpect(status().isNotFound());
    }
    @Test void bloqueiaCancelamentoOuReagendamentoDentroDoPrazo() throws Exception {
        var i = instrucao(aluno(), instrutor(), LocalDateTime.now(clock).plusHours(23));
        mvc.perform(delete("/instrucao/{id}", i.getId())).andExpect(status().isBadRequest());
        mvc.perform(body(put("/instrucao"), Map.of("id", i.getId(), "dataHora", horario().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm"))))).andExpect(status().isBadRequest());
    }
    @Test void cancelamentoLiberaHorarioELimiteDiario() throws Exception {
        var a = aluno(); var i = instrutor(); var aula = instrucao(a, i, horario());
        mvc.perform(body(post("/instrucao/{id}/cancelamento", aula.getId()), Map.of("motivo", "INSTRUTOR_CANCELOU"))).andExpect(status().isNoContent());
        mvc.perform(body(post("/instrucao"), agendamento(a, i, horario()))).andExpect(status().isCreated());
    }
    @Test void instrucaoCanceladaNaoPodeSerReabertaOuCanceladaNovamente() throws Exception {
        var i = instrucao(aluno(), instrutor(), horario()); i.cancelar(MotivoCancelamento.OUTROS, LocalDateTime.now(clock)); instrucoes.flush();
        mvc.perform(delete("/instrucao/{id}", i.getId())).andExpect(status().isBadRequest());
        mvc.perform(body(put("/instrucao"), Map.of("id", i.getId(), "dataHora", horario().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm"))))).andExpect(status().isBadRequest());
    }
    @Test void reagendamentoTambemValidaConflitos() throws Exception {
        var i = instrutor(); var outra = instrucao(aluno(), i, horario().plusHours(1)); var aula = instrucao(aluno(), i, horario());
        var update = new java.util.HashMap<>(agendamento(aula.getAluno(), i, outra.getDataHora())); update.put("id", aula.getId());
        mvc.perform(body(put("/instrucao"), update)).andExpect(status().isBadRequest());
    }
}
