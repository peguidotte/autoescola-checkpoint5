package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrucao;

import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.Especialidade;
import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record DadosAtualizacaoInstrucao(@NotNull @Positive Long id,
    @Positive @JsonAlias("id_aluno") Long idAluno,
    @Positive @JsonAlias("id_instrutor") Long idInstrutor,
    Especialidade especialidade,
    @NotNull @Future @JsonFormat(pattern = "dd/MM/yyyy - HH:mm")
    @JsonAlias("data_hora") LocalDateTime dataHora) {}
