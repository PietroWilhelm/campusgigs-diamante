package br.com.fiap.campusgigs.controller;

import br.com.fiap.campusgigs.dto.request.UsuarioRequest;
import br.com.fiap.campusgigs.dto.response.UsuarioResponse;
import br.com.fiap.campusgigs.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse cadastrar(@Valid @RequestBody UsuarioRequest request) {
        return usuarioService.cadastrar(request);
    }
}
