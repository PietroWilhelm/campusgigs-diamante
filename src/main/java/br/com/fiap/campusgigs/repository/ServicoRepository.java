package br.com.fiap.campusgigs.repository;

import br.com.fiap.campusgigs.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicoRepository extends JpaRepository<Servico, Long> {
}
