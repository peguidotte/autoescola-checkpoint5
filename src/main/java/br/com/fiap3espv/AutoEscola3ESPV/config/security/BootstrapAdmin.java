package br.com.fiap3espv.AutoEscola3ESPV.config.security;

import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Usuario;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.UsuarioRepository;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.Papel;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
@Component @RequiredArgsConstructor @ConditionalOnProperty(name = "app.bootstrap.enabled", havingValue = "true")
public class BootstrapAdmin implements ApplicationRunner {
    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    @Value("${app.bootstrap.login}") private String login;
    @Value("${app.bootstrap.senha}") private String senha;
    @Override public void run(ApplicationArguments args) {
        if (senha == null || senha.length() < 8) throw new IllegalStateException("ADMIN_PASSWORD deve ter ao menos 8 caracteres");
        if (repository.findByLogin(login).isEmpty()) repository.save(new Usuario(null, login, encoder.encode(senha), Papel.ADMIN, true));
    }
}
