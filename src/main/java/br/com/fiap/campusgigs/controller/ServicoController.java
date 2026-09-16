package br.com.fiap.campusgigs.controller;

import br.com.fiap.campusgigs.dto.request.ServicoRequest;
import br.com.fiap.campusgigs.dto.response.ServicoResponse;
import br.com.fiap.campusgigs.service.ServicoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicos")
public class ServicoController {

    private final ServicoService servicoService;

    public ServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServicoResponse publicar(@Valid @RequestBody ServicoRequest request, Authentication authentication) {
        return servicoService.publicar(request, authentication);
    }

    @GetMapping
    public List<ServicoResponse> listar() {
        return servicoService.listar();
    }

    @PutMapping("/{id}")
    public ServicoResponse editar(@PathVariable Long id, @Valid @RequestBody ServicoRequest request,
                                   Authentication authentication) {
        return servicoService.editar(id, request, authentication);
    }

    @PostMapping("/{id}/encerrar")
    public ServicoResponse encerrar(@PathVariable Long id, Authentication authentication) {
        return servicoService.encerrar(id, authentication);
    }
}
