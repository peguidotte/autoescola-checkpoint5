package br.com.fiap3espv.AutoEscola3ESPV.config.security;

import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.UsuarioRepository;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
@Component @RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {
    private final TokenService tokens;
    private final UsuarioRepository repository;
    @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        String header = req.getHeader("Authorization");
        if (header != null) {
            if (!header.startsWith("Bearer ") || header.length() <= 7) { res.sendError(401, "Token inválido"); return; }
            try {
                var usuario = repository.findByLogin(tokens.getSubject(header.substring(7)));
                if (usuario.isEmpty() || !usuario.get().isEnabled()) { res.sendError(401, "Usuário inválido ou inativo"); return; }
                var principal = usuario.get();
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
            } catch (JWTVerificationException | IllegalArgumentException e) {
                SecurityContextHolder.clearContext(); res.sendError(401, "Token inválido ou expirado"); return;
            }
        }
        chain.doFilter(req, res);
    }
}
