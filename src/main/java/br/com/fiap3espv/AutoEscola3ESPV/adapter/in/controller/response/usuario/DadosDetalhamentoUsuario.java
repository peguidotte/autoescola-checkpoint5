package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.usuario;

import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Usuario;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.Papel;
public record DadosDetalhamentoUsuario(Long id, String login, Papel papel, boolean ativo) {
    public DadosDetalhamentoUsuario(Usuario usuario) {
        this(usuario.getId(), usuario.getLogin(), usuario.getPapel(), usuario.isAtivo());
    }
}
