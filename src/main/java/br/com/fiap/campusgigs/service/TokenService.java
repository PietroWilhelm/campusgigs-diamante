package br.com.fiap.campusgigs.service;

import br.com.fiap.campusgigs.model.Usuario;
import br.com.fiap.campusgigs.security.AutenticadoService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

// Mesmo padrão do tutorial de JWT (JwtEncoder + JwtClaimsSet), com a claim extra
// "role" (equivalente ao "heroType" do desafio extra) usada para autorização.
@Service
public class TokenService {

    private final JwtEncoder encoder;
    private final AutenticadoService autenticadoService;

    public TokenService(JwtEncoder encoder, AutenticadoService autenticadoService) {
        this.encoder = encoder;
        this.autenticadoService = autenticadoService;
    }

    public String generateToken(Authentication authentication) {
        Usuario usuario = autenticadoService.usuarioLogado(authentication);

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("campusgigs-api")
                .issuedAt(now)
                .expiresAt(now.plus(2, ChronoUnit.HOURS))
                .subject(usuario.getEmail())
                .claim("role", usuario.getRole().name())
                .claim("nome", usuario.getNome())
                .build();

        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
