package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.aluno;

import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.endereco.dto.DadosEndereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record DadosAtualizacaoAluno(@NotNull @Positive Long id, @Size(min = 1, max = 100) String nome,
    @Size(min = 1, max = 20) String telefone,
    @Null(message = "O e-mail do aluno não pode ser alterado") String email,
    @Valid DadosEndereco endereco,
    @Null(message = "O CPF do aluno não pode ser alterado") String cpf) {}
