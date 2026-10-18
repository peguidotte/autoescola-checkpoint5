package br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.agendamento;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.contract.ValidadorAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.*;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.ValidacaoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
@Component @RequiredArgsConstructor
public class ValidadorHoraInteira implements ValidadorAgendamento {

    @Override public void validar(DadosAgendamento dados, Long ignorarId) {
        var data = dados.dataHora();
if (data.getMinute() != 0 || data.getSecond() != 0 || data.getNano() != 0)
    throw new ValidacaoException("O horário deve ser preenchido em horas inteiras");
    }
}
