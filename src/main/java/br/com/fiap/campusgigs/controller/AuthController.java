package br.com.fiap.campusgigs.controller;

import br.com.fiap.campusgigs.dto.request.LoginRequest;
import br.com.fiap.campusgigs.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// Login via JSON (email/senha no corpo), igual ao PetPulse - diferente do tutorial
// original do professor, que usava HTTP Basic. TokenResponse fica como record
// aninhado aqui mesmo (não como DTO separado), pois só é usado neste endpoint.
@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public record TokenResponse(String token) {}

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha())
        );
        return new TokenResponse(tokenService.generateToken(authentication));
    }
}
