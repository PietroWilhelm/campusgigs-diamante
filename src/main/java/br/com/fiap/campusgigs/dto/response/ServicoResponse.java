package br.com.fiap.campusgigs.dto.response;

import br.com.fiap.campusgigs.enums.SituacaoServico;
import br.com.fiap.campusgigs.model.Servico;

import java.math.BigDecimal;

public record ServicoResponse(
        Long id,
        String titulo,
        String descricao,
        String categoria,
        BigDecimal preco,
        SituacaoServico situacao,
        Long prestadorId,
        String prestadorNome
) {
    public static ServicoResponse fromEntity(Servico servico) {
        return new ServicoResponse(
                servico.getId(),
                servico.getTitulo(),
                servico.getDescricao(),
                servico.getCategoria(),
                servico.getPreco(),
                servico.getSituacao(),
                servico.getPrestador().getId(),
                servico.getPrestador().getNome()
        );
    }
}
