package br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.agendamento;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.contract.ValidadorAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.*;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.ValidacaoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
@Component @RequiredArgsConstructor
public class ValidadorLimiteDiarioAluno implements ValidadorAgendamento {
    private final InstrucaoRepository repository;
    @Override public void validar(DadosAgendamento dados, Long ignorarId) {
        var inicio = dados.dataHora().toLocalDate().atStartOfDay();
        if (repository.contarInstrucoesNoDia(dados.idAluno(), inicio, inicio.plusDays(1), ignorarId) >= 2)
            throw new ValidacaoException("Permitidas no máximo duas instruções diárias por aluno");
    }
}
