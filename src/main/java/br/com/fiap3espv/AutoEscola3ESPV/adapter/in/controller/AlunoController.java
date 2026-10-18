package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.aluno.*;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.aluno.DadosDetalhamentoAluno;
import br.com.fiap3espv.AutoEscola3ESPV.application.service.AlunoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController @RequestMapping("/aluno") @RequiredArgsConstructor @Tag(name = "Alunos")
public class AlunoController {
    private final AlunoService service;
    @PostMapping @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DadosDetalhamentoAluno> cadastrar(@RequestBody @Valid DadosCadastroAluno dados, UriComponentsBuilder uri) {
        var aluno = service.cadastrar(dados);
        return ResponseEntity.created(uri.path("/aluno/{id}").buildAndExpand(aluno.id()).toUri()).body(aluno);
    }
    @GetMapping
    public Page<DadosDetalhamentoAluno> listar(@ParameterObject @PageableDefault(sort = "nome") Pageable pageable) { return service.listar(pageable); }
    @GetMapping("/{id}") public DadosDetalhamentoAluno detalhar(@PathVariable Long id) { return service.detalhar(id); }
    @PutMapping @PreAuthorize("hasRole('ADMIN')")
    public DadosDetalhamentoAluno atualizar(@RequestBody @Valid DadosAtualizacaoAluno dados) { return service.atualizar(dados); }
    @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) { service.excluir(id); return ResponseEntity.noContent().build(); }
}
