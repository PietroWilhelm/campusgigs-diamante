package br.com.fiap.campusgigs.dto.response;

import br.com.fiap.campusgigs.enums.SituacaoContratacao;
import br.com.fiap.campusgigs.model.Contratacao;

import java.time.LocalDateTime;

public record ContratacaoResponse(
        Long id,
        Long servicoId,
        String servicoTitulo,
        Long contratanteId,
        String contratanteNome,
        SituacaoContratacao situacao,
        LocalDateTime dataContratacao
) {
    public static ContratacaoResponse fromEntity(Contratacao contratacao) {
        return new ContratacaoResponse(
                contratacao.getId(),
                contratacao.getServico().getId(),
                contratacao.getServico().getTitulo(),
                contratacao.getContratante().getId(),
                contratacao.getContratante().getNome(),
                contratacao.getSituacao(),
                contratacao.getDataContratacao()
        );
    }
}
