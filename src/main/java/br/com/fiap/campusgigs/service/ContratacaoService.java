package br.com.fiap.campusgigs.service;

import br.com.fiap.campusgigs.dto.response.ContratacaoResponse;
import br.com.fiap.campusgigs.enums.SituacaoContratacao;
import br.com.fiap.campusgigs.enums.SituacaoServico;
import br.com.fiap.campusgigs.model.Contratacao;
import br.com.fiap.campusgigs.model.Servico;
import br.com.fiap.campusgigs.model.Usuario;
import br.com.fiap.campusgigs.repository.ContratacaoRepository;
import br.com.fiap.campusgigs.security.AutenticadoService;
import br.com.fiap.campusgigs.validation.ValidationHandler.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class ContratacaoService {

    private final ContratacaoRepository contratacaoRepository;
    private final ServicoService servicoService;
    private final AutenticadoService autenticadoService;

    public ContratacaoService(ContratacaoRepository contratacaoRepository, ServicoService servicoService,
                               AutenticadoService autenticadoService) {
        this.contratacaoRepository = contratacaoRepository;
        this.servicoService = servicoService;
        this.autenticadoService = autenticadoService;
    }

    public ContratacaoResponse contratar(Long servicoId, Authentication authentication) {
        Servico servico = servicoService.buscarPorId(servicoId);
        Usuario contratante = autenticadoService.usuarioLogado(authentication);

        if (servico.getSituacao() != SituacaoServico.ATIVO) {
            throw new BusinessException("Este serviço não está ativo e não pode ser contratado");
        }

        if (servico.getPrestador().getId().equals(contratante.getId())) {
            throw new BusinessException("Você não pode contratar o próprio serviço");
        }

        Contratacao contratacao = new Contratacao();
        contratacao.setServico(servico);
        contratacao.setContratante(contratante);
        contratacao.setSituacao(SituacaoContratacao.SOLICITADA);

        return ContratacaoResponse.fromEntity(contratacaoRepository.save(contratacao));
    }
}
