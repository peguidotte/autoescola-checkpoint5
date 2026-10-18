package br.com.fiap3espv.AutoEscola3ESPV.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import java.net.http.HttpClient;
import java.time.Duration;
@Configuration
public class HttpClientConfig {
    @Bean public RestClient viaCepRestClient(@Value("${api.viacep.base-url}") String baseUrl,
        @Value("${api.viacep.connect-timeout}") Duration connectTimeout,
        @Value("${api.viacep.read-timeout}") Duration readTimeout) {
        var factory = new JdkClientHttpRequestFactory(HttpClient.newBuilder().connectTimeout(connectTimeout).build());
        factory.setReadTimeout(readTimeout);
        return RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }
}
