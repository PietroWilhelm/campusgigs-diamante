package br.com.fiap.campusgigs.dto.response;

// Representa o JSON retornado por https://viacep.com.br/ws/{cep}/json
// Quando o CEP não existe, a ViaCEP responde com {"erro": true} (sem os demais campos).
public record ViaCepResponse(
        String cep,
        String logradouro,
        String bairro,
        String localidade,
        String uf,
        Boolean erro
) {}
