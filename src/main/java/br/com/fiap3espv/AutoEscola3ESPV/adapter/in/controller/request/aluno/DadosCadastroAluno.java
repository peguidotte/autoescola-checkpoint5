package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.aluno;

import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.endereco.dto.DadosEndereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record DadosCadastroAluno(@NotBlank @Size(max = 100) String nome,
    @NotBlank @Size(max = 20) String telefone, @NotBlank @Email @Size(max = 100) String email,
    @NotBlank @Pattern(regexp = "\\d{11}") String cpf, @NotNull @Valid DadosEndereco endereco) {}
