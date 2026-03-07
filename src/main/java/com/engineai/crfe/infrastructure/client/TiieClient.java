package com.engineai.crfe.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

@Component
public class TiieClient {

    private final RestTemplate restTemplate;
    private final String tiieBaseUrl;

    public TiieClient(
            RestTemplate restTemplate,
            @Value("${services.tiie.base-url}") String tiieBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.tiieBaseUrl = tiieBaseUrl;
    }

    public List<TransactionResponse> getTransactionsByUserId(Long userId, String bearerToken) {
        String url = tiieBaseUrl + "/api/transactions?userId=" + userId;

        RequestEntity<Void> request = RequestEntity
                .get(URI.create(url))
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .build();

        return restTemplate.exchange(
                request,
                new ParameterizedTypeReference<List<TransactionResponse>>() {}
        ).getBody();
    }
}