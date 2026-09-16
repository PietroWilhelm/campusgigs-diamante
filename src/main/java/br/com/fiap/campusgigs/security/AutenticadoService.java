package br.com.fiap.campusgigs.security;

import br.com.fiap.campusgigs.model.Usuario;
import br.com.fiap.campusgigs.repository.UsuarioRepository;
import br.com.fiap.campusgigs.validation.ValidationHandler.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

// Concentra o acesso ao usuário autenticado (equivalente ao pacote "security" do
// PetPulse, que representa o usuário logado). Usado pelos services para saber
// quem está fazendo a requisição e checar se é ADMIN.
@Component
public class AutenticadoService {

    private final UsuarioRepository usuarioRepository;

    public AutenticadoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario usuarioLogado(Authentication authentication) {
        return usuarioRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado"));
    }

    public boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
