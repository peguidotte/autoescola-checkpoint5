package br.com.fiap3espv.AutoEscola3ESPV.config.security;

import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.*;
@Service
public class TokenService {
    private final Algorithm algorithm;
    private final Clock clock;
    private static final String ISSUER = "AutoEscola3ESPV";
    public TokenService(@Value("${api.security.token.secret}") String secret, Clock clock) {
        this.algorithm = Algorithm.HMAC256(secret); this.clock = clock;
    }
    public String gerarToken(Usuario usuario) {
        return JWT.create().withIssuer(ISSUER).withSubject(usuario.getLogin())
            .withIssuedAt(clock.instant()).withExpiresAt(clock.instant().plusSeconds(3600)).sign(algorithm);
    }
    public String getSubject(String token) {
        return JWT.require(algorithm).withIssuer(ISSUER).build().verify(token).getSubject();
    }
}
