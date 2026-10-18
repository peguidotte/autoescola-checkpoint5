package br.com.fiap3espv.AutoEscola3ESPV.application.service;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.usuario.*;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.usuario.DadosDetalhamentoUsuario;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Usuario;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.UsuarioRepository;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.ValidacaoException;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.Papel;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    @Transactional public DadosDetalhamentoUsuario cadastrar(DadosCadastroUsuario d) {
        return new DadosDetalhamentoUsuario(repository.save(new Usuario(null, d.login(), encoder.encode(d.senha()), d.papel(), true)));
    }
    @Transactional(readOnly = true) public Page<DadosDetalhamentoUsuario> listar(Pageable pageable) {
        return repository.findAllByAtivoTrue(pageable).map(DadosDetalhamentoUsuario::new);
    }
    @Transactional(readOnly = true) public DadosDetalhamentoUsuario detalhar(Long id) { return new DadosDetalhamentoUsuario(buscar(id)); }
    @Transactional public DadosDetalhamentoUsuario atualizar(DadosAtualizacaoUsuario d) {
        var admins = repository.bloquearAdministradores(Papel.ADMIN);
        Usuario usuario = buscar(d.id());
        if (usuario.isAtivo() && usuario.getPapel() == Papel.ADMIN && d.papel() == Papel.USER && admins.size() <= 1)
            throw new ValidacaoException("Não é permitido remover o último administrador ativo");
        usuario.atualizar(d.login(), d.senha() == null ? null : encoder.encode(d.senha()), d.papel());
        return new DadosDetalhamentoUsuario(usuario);
    }
    @Transactional public void excluir(Long id) {
        var admins = repository.bloquearAdministradores(Papel.ADMIN);
        Usuario usuario = buscar(id);
        if (usuario.isAtivo() && usuario.getPapel() == Papel.ADMIN && admins.size() <= 1)
            throw new ValidacaoException("Não é permitido remover o último administrador ativo");
        usuario.excluir();
    }
    private Usuario buscar(Long id) { return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado")); }
}
