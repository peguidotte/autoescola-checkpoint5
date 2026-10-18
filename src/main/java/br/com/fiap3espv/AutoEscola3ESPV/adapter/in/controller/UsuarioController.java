package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.usuario.*;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.usuario.DadosDetalhamentoUsuario;
import br.com.fiap3espv.AutoEscola3ESPV.application.service.UsuarioService;
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

@RestController @RequestMapping("/usuario") @RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") @Tag(name = "Usuários")
public class UsuarioController {
    private final UsuarioService service;
    @PostMapping public ResponseEntity<DadosDetalhamentoUsuario> cadastrar(@RequestBody @Valid DadosCadastroUsuario dados, UriComponentsBuilder uri) {
        var usuario = service.cadastrar(dados);
        return ResponseEntity.created(uri.path("/usuario/{id}").buildAndExpand(usuario.id()).toUri()).body(usuario);
    }
    @GetMapping public Page<DadosDetalhamentoUsuario> listar(@ParameterObject @PageableDefault(sort = "login") Pageable pageable) { return service.listar(pageable); }
    @GetMapping("/{id}") public DadosDetalhamentoUsuario detalhar(@PathVariable Long id) { return service.detalhar(id); }
    @PutMapping public DadosDetalhamentoUsuario atualizar(@RequestBody @Valid DadosAtualizacaoUsuario dados) { return service.atualizar(dados); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable Long id) { service.excluir(id); return ResponseEntity.noContent().build(); }
}
