package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrucao.*;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.instrucao.DetalhamentoAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.application.service.AgendaDeInstrucoes;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.MotivoCancelamento;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController @RequestMapping("/instrucao") @RequiredArgsConstructor @Tag(name = "Instruções")
public class InstrucaoController {
    private final AgendaDeInstrucoes agenda;
    @PostMapping @Operation(summary = "Agendar uma instrução de uma hora")
    public ResponseEntity<DetalhamentoAgendamento> agendarInstrucao(@RequestBody @Valid DadosAgendamento dados, UriComponentsBuilder uri) {
        var instrucao = agenda.agendar(dados);
        return ResponseEntity.created(uri.path("/instrucao/{id}").buildAndExpand(instrucao.id()).toUri()).body(instrucao);
    }
    @GetMapping public Page<DetalhamentoAgendamento> listar(@ParameterObject @PageableDefault(sort = "dataHora") Pageable pageable) { return agenda.listar(pageable); }
    @GetMapping("/{id}") public DetalhamentoAgendamento detalhar(@PathVariable Long id) { return agenda.detalhar(id); }
    @PutMapping @Operation(summary = "Reagendar sem violar as regras de agendamento ou o prazo de cancelamento")
    public DetalhamentoAgendamento atualizar(@RequestBody @Valid DadosAtualizacaoInstrucao dados) { return agenda.atualizar(dados); }
    @DeleteMapping("/{id}") @Operation(summary = "Cancelar uma instrução, preservando o histórico")
    public ResponseEntity<Void> cancelar(@PathVariable Long id,
        @RequestParam(defaultValue = "OUTROS") MotivoCancelamento motivo) {
        agenda.cancelar(id, motivo); return ResponseEntity.noContent().build();
    }
    @PostMapping("/{id}/cancelamento") @Operation(summary = "Cancelar uma instrução informando o motivo no corpo")
    public ResponseEntity<Void> cancelarComMotivo(@PathVariable Long id, @RequestBody @Valid DadosCancelamento dados) {
        agenda.cancelar(id, dados.motivo()); return ResponseEntity.noContent().build();
    }
}
