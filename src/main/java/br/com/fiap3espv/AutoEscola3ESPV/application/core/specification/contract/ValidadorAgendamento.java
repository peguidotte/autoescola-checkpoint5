package br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.contract;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrucao.DadosAgendamento;
public interface ValidadorAgendamento {
    void validar(DadosAgendamento dados, Long ignorarId);
    default void validar(DadosAgendamento dados) { validar(dados, null); }
}
