package br.com.fiap.campusgigs.model;

import br.com.fiap.campusgigs.enums.SituacaoContratacao;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "contratacao")
public class Contratacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico;

    @ManyToOne(optional = false)
    @JoinColumn(name = "contratante_id", nullable = false)
    private Usuario contratante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SituacaoContratacao situacao = SituacaoContratacao.SOLICITADA;

    @Column(name = "data_contratacao", nullable = false)
    private LocalDateTime dataContratacao = LocalDateTime.now();
}
