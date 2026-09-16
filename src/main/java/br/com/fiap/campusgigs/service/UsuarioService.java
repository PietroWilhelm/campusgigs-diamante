package br.com.fiap.campusgigs.service;

import br.com.fiap.campusgigs.dto.request.UsuarioRequest;
import br.com.fiap.campusgigs.dto.response.UsuarioResponse;
import br.com.fiap.campusgigs.enums.Role;
import br.com.fiap.campusgigs.model.Usuario;
import br.com.fiap.campusgigs.repository.UsuarioRepository;
import br.com.fiap.campusgigs.validation.ValidationHandler.BusinessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioResponse cadastrar(UsuarioRequest request) {
        if (usuarioRepository.existsByEmailIgnoreCase(request.email())) {
            throw new BusinessException("Já existe um usuário cadastrado com este e-mail");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuario.setRole(Role.USER);
        usuario.setCep(request.cep());

        usuario = usuarioRepository.save(usuario);
        return UsuarioResponse.fromEntity(usuario);
    }
}
