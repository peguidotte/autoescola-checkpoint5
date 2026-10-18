package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.instrutor;

import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.endereco.dto.DadosEndereco;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.Especialidade;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Instrutor;

public record DadosDetalhamentoInstrutor(
        Long id,
        String nome,
        String telefone,
        String email,
        String cnh,
        Especialidade especialidade,
        DadosEndereco endereco,
        boolean ativo) {
}