package br.com.fiap.campusgigs.service;

import br.com.fiap.campusgigs.dto.request.ServicoRequest;
import br.com.fiap.campusgigs.dto.response.ServicoResponse;
import br.com.fiap.campusgigs.enums.SituacaoServico;
import br.com.fiap.campusgigs.model.Servico;
import br.com.fiap.campusgigs.model.Usuario;
import br.com.fiap.campusgigs.repository.ServicoRepository;
import br.com.fiap.campusgigs.security.AutenticadoService;
import br.com.fiap.campusgigs.validation.ValidationHandler.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final AutenticadoService autenticadoService;

    public ServicoService(ServicoRepository servicoRepository, AutenticadoService autenticadoService) {
        this.servicoRepository = servicoRepository;
        this.autenticadoService = autenticadoService;
    }

    public ServicoResponse publicar(ServicoRequest request, Authentication authentication) {
        Usuario prestador = autenticadoService.usuarioLogado(authentication);

        Servico servico = new Servico();
        servico.setPrestador(prestador);
        servico.setTitulo(request.titulo());
        servico.setDescricao(request.descricao());
        servico.setCategoria(request.categoria());
        servico.setPreco(request.preco());
        servico.setSituacao(SituacaoServico.ATIVO);

        return ServicoResponse.fromEntity(servicoRepository.save(servico));
    }

    public List<ServicoResponse> listar() {
        return servicoRepository.findAll().stream()
                .map(ServicoResponse::fromEntity)
                .toList();
    }

    public ServicoResponse editar(Long id, ServicoRequest request, Authentication authentication) {
        Servico servico = buscarPorId(id);
        garantirDonoOuAdmin(servico, authentication);

        servico.setTitulo(request.titulo());
        servico.setDescricao(request.descricao());
        servico.setCategoria(request.categoria());
        servico.setPreco(request.preco());

        return ServicoResponse.fromEntity(servicoRepository.save(servico));
    }

    public ServicoResponse encerrar(Long id, Authentication authentication) {
        Servico servico = buscarPorId(id);
        garantirDonoOuAdmin(servico, authentication);

        servico.setSituacao(SituacaoServico.ENCERRADO);
        return ServicoResponse.fromEntity(servicoRepository.save(servico));
    }

    Servico buscarPorId(Long id) {
        return servicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado: " + id));
    }

    // Só o dono do serviço pode editar/encerrar, exceto ADMIN, que pode encerrar qualquer um.
    // Fica no service (e não em @PreAuthorize) porque depende do dado - quem é o
    // prestador do registro - e não só do papel do usuário logado.
    private void garantirDonoOuAdmin(Servico servico, Authentication authentication) {
        boolean isDono = servico.getPrestador().getEmail().equalsIgnoreCase(authentication.getName());

        if (!autenticadoService.isAdmin(authentication) && !isDono) {
            throw new AccessDeniedException("Apenas o prestador do serviço ou um ADMIN pode realizar esta operação");
        }
    }
}
