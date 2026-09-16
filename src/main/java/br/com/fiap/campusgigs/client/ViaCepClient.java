package br.com.fiap.campusgigs.client;

import br.com.fiap.campusgigs.dto.response.ViaCepResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

// Cliente HTTP declarativo (Spring 6 HttpExchange).
// O Spring gera a implementação em tempo de execução a partir dessa interface,
// registrada como bean em RestClientConfig usando HttpServiceProxyFactory.
// Requisição feita: GET https://viacep.com.br/ws/{cep}/json
public interface ViaCepClient {

    @GetExchange("/{cep}/json")
    ViaCepResponse buscarPorCep(@PathVariable String cep);
}
