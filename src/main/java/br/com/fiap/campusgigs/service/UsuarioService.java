package br.com.fiap.campusgigs.service;

import br.com.fiap.campusgigs.client.ViaCepClient;
import br.com.fiap.campusgigs.dto.request.UsuarioRequest;
import br.com.fiap.campusgigs.dto.response.UsuarioResponse;
import br.com.fiap.campusgigs.dto.response.ViaCepResponse;
import br.com.fiap.campusgigs.enums.Role;
import br.com.fiap.campusgigs.model.Usuario;
import br.com.fiap.campusgigs.repository.UsuarioRepository;
import br.com.fiap.campusgigs.validation.ValidationHandler.BusinessException;
import br.com.fiap.campusgigs.validation.ValidationHandler.CepNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ViaCepClient viaCepClient;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, ViaCepClient viaCepClient) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.viaCepClient = viaCepClient;
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

        preencherEnderecoPorCep(usuario, request.cep());

        usuario = usuarioRepository.save(usuario);
        return UsuarioResponse.fromEntity(usuario);
    }

    // Consulta o serviço externo (ViaCEP) via HttpExchange para obter cidade/UF.
    // Se o CEP não existir, ou o serviço externo falhar/demorar, a operação é
    // interrompida com uma resposta clara em vez de salvar o usuário incompleto.
    private void preencherEnderecoPorCep(Usuario usuario, String cep) {
        ViaCepResponse resposta;
        try {
            resposta = viaCepClient.buscarPorCep(cep);
        } catch (RestClientException e) {
            throw new CepNotFoundException("Não foi possível consultar o CEP informado no momento. Tente novamente.");
        }

        if (resposta == null || Boolean.TRUE.equals(resposta.erro())) {
            throw new CepNotFoundException("CEP não encontrado: " + cep);
        }

        usuario.setCidade(resposta.localidade());
        usuario.setUf(resposta.uf());
    }
}
