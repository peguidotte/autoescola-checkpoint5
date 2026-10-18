package br.com.fiap3espv.AutoEscola3ESPV.application.service;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.aluno.*;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.aluno.DadosDetalhamentoAluno;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Aluno;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.AlunoRepository;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.AlunoNotFoundException;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.endereco.mapper.EnderecoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class AlunoService {
    private final AlunoRepository repository;
    private final EnderecoMapper mapper;
    @Transactional
    public DadosDetalhamentoAluno cadastrar(DadosCadastroAluno d) {
        return new DadosDetalhamentoAluno(repository.save(new Aluno(null, d.nome(), d.telefone(),
            d.email(), d.cpf(), mapper.toVo(d.endereco()), true)));
    }
    @Transactional(readOnly = true)
    public Page<DadosDetalhamentoAluno> listar(Pageable pageable) {
        return repository.findAllByAtivoTrue(pageable).map(DadosDetalhamentoAluno::new);
    }
    @Transactional(readOnly = true)
    public DadosDetalhamentoAluno detalhar(Long id) { return new DadosDetalhamentoAluno(buscar(id)); }
    @Transactional
    public DadosDetalhamentoAluno atualizar(DadosAtualizacaoAluno d) {
        Aluno aluno = buscar(d.id());
        aluno.atualizar(d.nome(), d.telefone(), d.email(), mapper.toVo(d.endereco()));
        return new DadosDetalhamentoAluno(aluno);
    }
    @Transactional
    public void excluir(Long id) { buscar(id).excluir(); }
    private Aluno buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new AlunoNotFoundException("Aluno não encontrado"));
    }
}
