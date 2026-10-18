package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrucao;

import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.MotivoCancelamento;
import jakarta.validation.constraints.*;
public record DadosCancelamento(@NotNull MotivoCancelamento motivo) {}
