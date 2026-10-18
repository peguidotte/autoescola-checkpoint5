package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrutor;

import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.endereco.dto.DadosEndereco;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.Especialidade;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record DadosAtualizacaoInstrutor(
        @NotNull
        Long id,
        String nome,
        String telefone,

        @Email
        String email,
        Especialidade especialidade,
        @jakarta.validation.Valid DadosEndereco endereco/*,
        boolean ativo*/) {
}