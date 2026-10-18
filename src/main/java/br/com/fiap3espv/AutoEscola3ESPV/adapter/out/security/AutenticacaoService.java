package br.com.fiap3espv.AutoEscola3ESPV.adapter.out.security;

import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
@Service @RequiredArgsConstructor
public class AutenticacaoService implements UserDetailsService {
    private final UsuarioRepository repository;
    @Override public UserDetails loadUserByUsername(String username) {
        return repository.findByLogin(username).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }
}
