package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.aluno;

import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Aluno;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.endereco.dto.DadosEndereco;

public record DadosDetalhamentoAluno(Long id, String nome, String telefone, String email,
                                    String cpf, DadosEndereco endereco, boolean ativo) {
    public DadosDetalhamentoAluno(Aluno aluno) {
        this(aluno.getId(), aluno.getNome(), aluno.getTelefone(), aluno.getEmail(),
             aluno.getCpf(), new DadosEndereco(aluno.getEndereco()), aluno.isAtivo());
    }
}
