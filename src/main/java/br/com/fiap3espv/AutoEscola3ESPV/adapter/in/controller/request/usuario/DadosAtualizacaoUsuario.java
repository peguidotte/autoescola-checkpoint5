package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.usuario;

import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.Papel;
import jakarta.validation.constraints.*;
public record DadosAtualizacaoUsuario(@NotNull @Positive Long id,
    @Pattern(regexp = ".*\\S.*") @Size(min = 1, max = 100) String login,
    @Size(min = 8, max = 72) String senha, Papel papel) {}
