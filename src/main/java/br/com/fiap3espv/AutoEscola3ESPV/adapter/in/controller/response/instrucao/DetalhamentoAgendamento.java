package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.instrucao;

import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Instrucao;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.*;
import com.fasterxml.jackson.annotation.*;
import java.time.LocalDateTime;

public record DetalhamentoAgendamento(Long id, Long idAluno, Long idInstrutor,
    @JsonProperty("nome_aluno") String nomeAluno, @JsonProperty("nome_instrutor") String nomeInstrutor,
    Especialidade especialidade,
    @JsonFormat(pattern = "dd/MM/yyyy - HH:mm") @JsonProperty("data_hora") LocalDateTime dataHora,
    String status, MotivoCancelamento motivoCancelamento,
    @JsonFormat(pattern = "dd/MM/yyyy - HH:mm") LocalDateTime canceladaEm) {
    public DetalhamentoAgendamento(Instrucao i) {
        this(i.getId(), i.getAluno().getId(), i.getInstrutor().getId(), i.getAluno().getNome(),
            i.getInstrutor().getNome(), i.getInstrutor().getEspecialidade(), i.getDataHora(),
            i.getMotivoCancelamento() == null ? "AGENDADA" : "CANCELADA", i.getMotivoCancelamento(), i.getCanceladaEm());
    }
}
