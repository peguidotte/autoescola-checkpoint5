package br.com.fiap3espv.AutoEscola3ESPV.application.service;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrucao.*;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.instrucao.DetalhamentoAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.*;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.contract.ValidadorAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.*;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.*;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.MotivoCancelamento;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service @RequiredArgsConstructor
public class AgendaDeInstrucoes {
    private final InstrucaoRepository repository;
    private final AlunoRepository alunoRepository;
    private final InstrutorRepository instrutorRepository;
    private final List<ValidadorAgendamento> validadores;
    private final Clock clock;
    @Value("${api.agenda.cancelamento-antecedencia:24h}") private Duration antecedenciaCancelamento;

    @Transactional public DetalhamentoAgendamento agendar(DadosAgendamento dados) {
        Aluno aluno = buscarAluno(dados.idAluno());
        Instrutor instrutor = escolherInstrutor(dados, null);
        validar(dados, instrutor, null);
        return new DetalhamentoAgendamento(repository.save(new Instrucao(null, aluno, instrutor, dados.dataHora())));
    }
    @Transactional(readOnly = true) public Page<DetalhamentoAgendamento> listar(Pageable pageable) {
        return repository.findAll(pageable).map(DetalhamentoAgendamento::new);
    }
    @Transactional(readOnly = true) public DetalhamentoAgendamento detalhar(Long id) {
        return new DetalhamentoAgendamento(repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Instrução não encontrada")));
    }
    @Transactional public DetalhamentoAgendamento atualizar(DadosAtualizacaoInstrucao d) {
        Instrucao instrucao = buscarParaAlteracao(d.id());
        validarPrazoAlteracao(instrucao);
        Long idAluno = d.idAluno() == null ? instrucao.getAluno().getId() : d.idAluno();
        // Omitir instrutor e especialidade mantém o instrutor atual; informar especialidade sorteia outro disponível.
        Long idInstrutor = d.idInstrutor() == null && d.especialidade() == null ? instrucao.getInstrutor().getId() : d.idInstrutor();
        var dados = new DadosAgendamento(idAluno, idInstrutor, d.especialidade(), d.dataHora());
        Aluno aluno = buscarAluno(idAluno);
        Instrutor instrutor = escolherInstrutor(dados, instrucao.getId());
        validar(dados, instrutor, instrucao.getId());
        instrucao.reagendar(aluno, instrutor, d.dataHora());
        return new DetalhamentoAgendamento(instrucao);
    }
    @Transactional public void cancelar(Long id, MotivoCancelamento motivo) {
        Instrucao instrucao = buscarParaAlteracao(id);
        validarPrazoAlteracao(instrucao);
        instrucao.cancelar(motivo, LocalDateTime.now(clock));
    }
    private void validarPrazoAlteracao(Instrucao instrucao) {
        if (instrucao.getMotivoCancelamento() != null) throw new ValidacaoException("Instrução já cancelada");
        if (instrucao.getDataHora().isBefore(LocalDateTime.now(clock).plus(antecedenciaCancelamento)))
            throw new ValidacaoException("Cancelamento ou reagendamento exige antecedência mínima de " + antecedenciaCancelamento.toHours() + " horas");
    }
    private Instrucao buscarParaAlteracao(Long id) {
        return repository.buscarParaAlteracao(id).orElseThrow(() -> new EntityNotFoundException("Instrução não encontrada"));
    }
    private Aluno buscarAluno(Long id) {
        // Serializa agendamentos do mesmo aluno, inclusive quando os instrutores são diferentes.
        return alunoRepository.buscarParaAgendamento(id).orElseThrow(() -> new AlunoNotFoundException("Aluno não encontrado"));
    }
    private void validar(DadosAgendamento dados, Instrutor instrutor, Long ignorarId) {
        if (dados.especialidade() != null && dados.especialidade() != instrutor.getEspecialidade())
            throw new ValidacaoException("Instrutor não possui a especialidade informada");
        var resolvidos = new DadosAgendamento(dados.idAluno(), instrutor.getId(), dados.especialidade(), dados.dataHora());
        validadores.forEach(v -> v.validar(resolvidos, ignorarId));
    }
    private Instrutor escolherInstrutor(DadosAgendamento dados, Long ignorarId) {
        if (dados.idInstrutor() != null)
            return instrutorRepository.buscarParaAgendamento(dados.idInstrutor()).orElseThrow(() -> new InstrutorNotFoundException("Instrutor não encontrado"));
        if (dados.especialidade() == null) throw new ValidacaoException("Especialidade é obrigatória quando o instrutor não é informado");
        // As linhas são bloqueadas sempre na mesma ordem; nenhuma seleção ignora um agendamento concorrente.
        var disponiveis = instrutorRepository.buscarCandidatos(dados.especialidade()).stream()
            .filter(i -> !repository.instrutorOcupado(i.getId(), dados.dataHora(), ignorarId)).toList();
        if (disponiveis.isEmpty()) throw new ValidacaoException("Nenhum instrutor disponível para a data/hora informada");
        return disponiveis.get(ThreadLocalRandom.current().nextInt(disponiveis.size()));
    }
}
