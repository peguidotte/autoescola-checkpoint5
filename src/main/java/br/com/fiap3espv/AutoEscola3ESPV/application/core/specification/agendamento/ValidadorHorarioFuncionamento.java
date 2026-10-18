package br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.agendamento;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.contract.ValidadorAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.*;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.ValidacaoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
@Component @RequiredArgsConstructor
public class ValidadorHorarioFuncionamento implements ValidadorAgendamento {

    @Override public void validar(DadosAgendamento dados, Long ignorarId) {
        var data = dados.dataHora();
if (data.getDayOfWeek() == java.time.DayOfWeek.SUNDAY || data.getHour() < 6 || data.getHour() > 20)
    throw new ValidacaoException("Tentativa de agendamento fora do horário de funcionamento");
    }
}
