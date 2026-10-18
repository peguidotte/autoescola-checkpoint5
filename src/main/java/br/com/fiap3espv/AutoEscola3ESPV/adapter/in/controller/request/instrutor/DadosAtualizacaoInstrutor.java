package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrutor;

import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.endereco.dto.DadosEndereco;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.Especialidade;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.NotNull;

public record DadosAtualizacaoInstrutor(
        @NotNull
        Long id,
        String nome,
        String telefone,

        @Null(message = "O e-mail do instrutor não pode ser alterado")
        String email,
        @Null(message = "A especialidade do instrutor não pode ser alterada")
        Especialidade especialidade,
        @jakarta.validation.Valid DadosEndereco endereco,
        @Null(message = "A CNH do instrutor não pode ser alterada") String cnh) {
}
