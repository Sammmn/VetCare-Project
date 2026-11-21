package com.backend.vetcare.security;

import com.backend.vetcare.config.JwtConfig;
import com.backend.vetcare.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final Clock clock;
    private final long expiracaoMinutos;

    public JwtService(JwtEncoder encoder,
                      Clock clock,
                      @Value("${app.jwt.expiration-minutes:60}") long expiracaoMinutos) {
        this.encoder = encoder;
        this.clock = clock;
        this.expiracaoMinutos = expiracaoMinutos;
    }

    public TokenGerado gerar(Usuario usuario) {
        Instant agora = Instant.now(clock);
        Duration validade = Duration.ofMinutes(expiracaoMinutos);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(JwtConfig.ISSUER)
                .issuedAt(agora)
                .expiresAt(agora.plus(validade))
                .subject(String.valueOf(usuario.getId()))
                .claim("email", usuario.getEmail())
                .claim("nome", usuario.getNome())
                .claim("cargo", usuario.getCargo().name())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new TokenGerado(token, validade.toSeconds());
    }

    public record TokenGerado(String token, long expiraEmSegundos) {
    }
}
