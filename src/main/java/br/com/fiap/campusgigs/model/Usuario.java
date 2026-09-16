package br.com.fiap.campusgigs.model;

import br.com.fiap.campusgigs.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    // Senha sempre armazenada com hash (BCrypt) - nunca texto puro.
    @Column(nullable = false)
    private String senha;

    private String cep;

    private String cidade;

    private String uf;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;
}
