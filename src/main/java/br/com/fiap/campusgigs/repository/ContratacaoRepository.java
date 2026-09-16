package br.com.fiap.campusgigs.repository;

import br.com.fiap.campusgigs.model.Contratacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContratacaoRepository extends JpaRepository<Contratacao, Long> {
}
