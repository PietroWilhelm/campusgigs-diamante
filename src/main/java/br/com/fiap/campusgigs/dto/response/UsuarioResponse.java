package br.com.fiap.campusgigs.dto.response;

import br.com.fiap.campusgigs.enums.Role;
import br.com.fiap.campusgigs.model.Usuario;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String cidade,
        String uf,
        Role role
) {
    public static UsuarioResponse fromEntity(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCidade(),
                usuario.getUf(),
                usuario.getRole()
        );
    }
}
