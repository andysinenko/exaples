package ua.com.sinenko.examples.web.service;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import ua.com.sinenko.examples.web.dto.Dto;
import ua.com.sinenko.examples.web.dto.InternalDto;

import javax.net.ssl.SSLException;


/**
 * WebClient with authorization and ignoring certificate verification
 * */
@Service
public class MyWebClient {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SslContext sslContext = SslContextBuilder
            .forClient()
            .trustManager(InsecureTrustManagerFactory.INSTANCE)
            .build();

    private final HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));

    public MyWebClient() throws SSLException {

    }

    public Dto getResource(Long resourceId) {
        var userName = "username";
        var userPassword = "pawword";
        var endpoint = "https://localhost:7090/service/json/";

        var response = WebClient.builder().filter(ExchangeFilterFunctions
                        .basicAuthentication(userName, userPassword))
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build()
                .get()
                .uri(endpoint + resourceId)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(resp -> {
                    if (resp.statusCode().equals(HttpStatus.OK)) {
                        return resp.bodyToMono(InternalDto.class).map(e -> new Dto("ok", e));
                    } else {
                        return Mono.just(new Dto("error", null));
                    }
                }).block();

        return response;
    }
}
