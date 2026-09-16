package br.com.fiap.campusgigs.controller;

import br.com.fiap.campusgigs.dto.response.ContratacaoResponse;
import br.com.fiap.campusgigs.service.ContratacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/servicos/{servicoId}/contratacoes")
public class ContratacaoController {

    private final ContratacaoService contratacaoService;

    public ContratacaoController(ContratacaoService contratacaoService) {
        this.contratacaoService = contratacaoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContratacaoResponse contratar(@PathVariable Long servicoId, Authentication authentication) {
        return contratacaoService.contratar(servicoId, authentication);
    }
}
