package br.com.fiap3espv.AutoEscola3ESPV;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.application.service.AgendaDeInstrucoes;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

class ConcorrenciaAgendaTest extends BaseApiTest {
    @Autowired AgendaDeInstrucoes agenda;

    @Test @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void duasRequisicoesNaoReservamOMesmoInstrutor() throws Exception {
        var primeiro = aluno(); var segundo = aluno(); var instrutor = instrutor();
        try {
            disputar(new DadosAgendamento(primeiro.getId(), instrutor.getId(), null, horario()),
                new DadosAgendamento(segundo.getId(), instrutor.getId(), null, horario()));
        } finally {
            limpar(List.of(primeiro.getId(), segundo.getId()), List.of(instrutor.getId()));
        }
    }

    @Test @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void duasRequisicoesNaoAgendamDuasAulasDoAlunoNoMesmoDia() throws Exception {
        var aluno = aluno(); var primeiro = instrutor(); var segundo = instrutor();
        try {
            disputar(new DadosAgendamento(aluno.getId(), primeiro.getId(), null, horario()),
                new DadosAgendamento(aluno.getId(), segundo.getId(), null, horario().plusHours(1)));
        } finally {
            limpar(List.of(aluno.getId()), List.of(primeiro.getId(), segundo.getId()));
        }
    }

    private void disputar(DadosAgendamento primeiro, DadosAgendamento segundo) throws Exception {
        var executor = Executors.newFixedThreadPool(2);
        var iniciar = new CountDownLatch(1);
        try {
            Callable<String> a = () -> tentar(primeiro, iniciar);
            Callable<String> b = () -> tentar(segundo, iniciar);
            var resultadoA = executor.submit(a); var resultadoB = executor.submit(b);
            iniciar.countDown();
            assertThat(List.of(resultadoA.get(20, TimeUnit.SECONDS), resultadoB.get(20, TimeUnit.SECONDS)))
                .containsExactlyInAnyOrder("criada", "recusada");
        } finally { executor.shutdownNow(); }
    }

    private String tentar(DadosAgendamento dados, CountDownLatch iniciar) throws InterruptedException {
        iniciar.await();
        try { agenda.agendar(dados); return "criada"; }
        catch (ValidacaoException e) { return "recusada"; }
    }

    private void limpar(List<Long> idsAlunos, List<Long> idsInstrutores) {
        var ids = instrucoes.findAll().stream().filter(i -> idsAlunos.contains(i.getAluno().getId()))
            .map(i -> i.getId()).toList();
        instrucoes.deleteAllByIdInBatch(ids);
        alunos.deleteAllByIdInBatch(idsAlunos);
        instrutores.deleteAllByIdInBatch(idsInstrutores);
    }
}
