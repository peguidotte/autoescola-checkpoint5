package br.com.fiap3espv.AutoEscola3ESPV.application.service;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.mapper.InstrutorMapper;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrutor.DadosAtualizacaoInstrutor;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrutor.DadosCadastroInstrutor;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.instrutor.DadosDetalhamentoInstrutor;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.instrutor.DadosListagemInstrutores;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Instrutor;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.InstrutorRepository;

import br.com.fiap3espv.AutoEscola3ESPV.exception.type.InstrutorNotFoundException;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.endereco.mapper.EnderecoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InstrutorService {
    private final InstrutorRepository repository;
    private final InstrutorMapper mapper;
    private final EnderecoMapper enderecoMapper;

    @Transactional
    public DadosDetalhamentoInstrutor cadastrarInstrutor(DadosCadastroInstrutor dados) {
        Instrutor instrutor = mapper.toDomain(dados);
        Instrutor salvo = repository.save(instrutor);
        return mapper.toDetailsDto(salvo);
    }

    @Transactional(readOnly = true)
    public Page<DadosListagemInstrutores> listarInstrutores(Pageable pageable) {
        return repository
                .findAllByAtivoTrue(pageable)
                .map(mapper::toListDto);
    }

    @Transactional(readOnly = true)
    public DadosDetalhamentoInstrutor detalharInstrutor(Long id) {
        Instrutor instrutor = repository
                .findById(id)
                .orElseThrow(() ->
                        new InstrutorNotFoundException("ID do instrutor informado não existe!"));
        return mapper.toDetailsDto(instrutor);
    }

    @Transactional
    public DadosDetalhamentoInstrutor atualizarInstrutor(DadosAtualizacaoInstrutor dados) {
        Instrutor instrutor = repository.findById(dados.id()).orElseThrow(() -> new InstrutorNotFoundException("Instrutor não encontrado"));
        instrutor.atualizarInformacoes(
                dados.nome(),
                dados.telefone(),
                enderecoMapper.toVo(dados.endereco())
        );
        Instrutor salvo = repository.save(instrutor);
        return mapper.toDetailsDto(salvo);
    }

    @Transactional
    public void excluirInstrutor(Long id) {
        Instrutor instrutor = repository.findById(id).orElseThrow(() -> new InstrutorNotFoundException("Instrutor não encontrado"));
        instrutor.excluir();
        repository.save(instrutor);
    }
}
