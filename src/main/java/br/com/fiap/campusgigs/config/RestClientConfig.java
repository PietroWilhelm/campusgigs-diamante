package br.com.fiap.campusgigs.config;

import br.com.fiap.campusgigs.client.ViaCepClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

// Registra o ViaCepClient (interface HttpExchange) como bean gerenciado pelo Spring.
@Configuration
public class RestClientConfig {

    @Bean
    public ViaCepClient viaCepClient(@Value("${viacep.base-url}") String baseUrl) {
        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();

        return factory.createClient(ViaCepClient.class);
    }
}
