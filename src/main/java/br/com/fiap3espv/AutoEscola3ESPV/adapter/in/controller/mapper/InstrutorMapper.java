package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.mapper;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrutor.DadosCadastroInstrutor;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.instrutor.DadosDetalhamentoInstrutor;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.instrutor.DadosListagemInstrutores;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Instrutor;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.endereco.mapper.EnderecoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InstrutorMapper {
    private final EnderecoMapper enderecoMapper;

    public Instrutor toDomain(DadosCadastroInstrutor dados) {
        return new Instrutor(
                null,
                dados.nome(),
                dados.telefone(),
                dados.email(),
                dados.cnh(),
                dados.especialidade(),
                enderecoMapper.toVo(dados.endereco()),
                true
        );
    }

    public DadosDetalhamentoInstrutor toDetailsDto(Instrutor domain) {
        return new DadosDetalhamentoInstrutor(
                domain.getId(),
                domain.getNome(),
                domain.getTelefone(),
                domain.getEmail(),
                domain.getCnh(),
                domain.getEspecialidade(),
                enderecoMapper.toDto(domain.getEndereco()),
                domain.isAtivo()
        );
    }

    public DadosListagemInstrutores toListDto(Instrutor domain) {
        return new DadosListagemInstrutores(
                domain.getId(),
                domain.getNome(),
                domain.getEmail(),
                domain.getCnh(),
                domain.getEspecialidade()
        );
    }
}
