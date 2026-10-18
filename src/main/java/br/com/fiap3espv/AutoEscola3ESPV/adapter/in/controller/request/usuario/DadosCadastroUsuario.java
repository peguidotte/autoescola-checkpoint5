package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.usuario;

import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.Papel;
import jakarta.validation.constraints.*;
public record DadosCadastroUsuario(@NotBlank @Size(max = 100) String login,
    @NotBlank @Size(min = 8, max = 72) String senha, @NotNull Papel papel) {}
