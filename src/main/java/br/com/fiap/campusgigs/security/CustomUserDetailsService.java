package br.com.fiap.campusgigs.security;

import br.com.fiap.campusgigs.model.Usuario;
import br.com.fiap.campusgigs.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Substitui o InMemoryUserDetailsManager do tutorial: os usuários (com senha em BCrypt)
// vêm do banco de dados, permitindo cadastro real via POST /usuarios.
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        return User.withUsername(usuario.getEmail())
                .password(usuario.getSenha())
                .authorities("ROLE_" + usuario.getRole().name())
                .build();
    }
}
