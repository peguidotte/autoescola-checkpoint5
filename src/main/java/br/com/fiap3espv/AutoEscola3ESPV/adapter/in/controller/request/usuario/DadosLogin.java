package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.usuario;

import jakarta.validation.constraints.NotBlank;

public record DadosLogin(
        @NotBlank
        String login,

        @NotBlank
        String senha) {
}