package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrutor;

import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.endereco.dto.DadosEndereco;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.Especialidade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record DadosCadastroInstrutor(
        @NotBlank
        String nome,

        @NotBlank
        String telefone,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Pattern(regexp = "\\d{9,11}")
        String cnh,

        @NotNull
        Especialidade especialidade,

        @NotNull
        @Valid
        DadosEndereco endereco) {
}