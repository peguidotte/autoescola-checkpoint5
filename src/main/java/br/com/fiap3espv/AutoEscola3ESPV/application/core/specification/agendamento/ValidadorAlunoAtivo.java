package br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.agendamento;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.contract.ValidadorAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.*;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.ValidacaoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
@Component @RequiredArgsConstructor
public class ValidadorAlunoAtivo implements ValidadorAgendamento {
    private final AlunoRepository repository;
    @Override public void validar(DadosAgendamento dados, Long ignorarId) {
        if (!repository.existsByIdAndAtivoTrue(dados.idAluno()))
    throw new ValidacaoException("Instrução não pode ser agendada para aluno inativo");
    }
}
