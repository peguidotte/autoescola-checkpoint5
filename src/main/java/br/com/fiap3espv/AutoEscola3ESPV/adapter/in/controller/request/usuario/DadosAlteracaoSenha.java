package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DadosAlteracaoSenha(
    @NotBlank String senhaAtual,
    @NotBlank @Size(min = 8, max = 72) String novaSenha) {}
