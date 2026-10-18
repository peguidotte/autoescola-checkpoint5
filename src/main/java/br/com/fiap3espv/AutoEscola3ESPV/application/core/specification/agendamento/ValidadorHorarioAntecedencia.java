package br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.agendamento;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.contract.ValidadorAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.*;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.ValidacaoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
@Component @RequiredArgsConstructor
public class ValidadorHorarioAntecedencia implements ValidadorAgendamento {
    private final java.time.Clock clock;
    @Override public void validar(DadosAgendamento dados, Long ignorarId) {
        if (dados.dataHora().isBefore(java.time.LocalDateTime.now(clock).plusMinutes(30)))
    throw new ValidacaoException("Necessária antecedência mínima de 30 minutos para agendamento");
    }
}
