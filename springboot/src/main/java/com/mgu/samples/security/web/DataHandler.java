package com.mgu.samples.security.web;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class DataHandler {
    public Mono<ServerResponse> getData(ServerRequest serverRequest) {
        return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                .body(Flux.just("one", "two", "three", "four"), List.class);
    }
}
