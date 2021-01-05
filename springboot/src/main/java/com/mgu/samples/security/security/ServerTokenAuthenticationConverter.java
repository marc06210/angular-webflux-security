package com.mgu.samples.security.security;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * This class is in charge of extracting the data required for authentication
 * from the ServerWebExchange.
 * It then returns an Authentication object holding the JWT
 */
@Component
public class ServerTokenAuthenticationConverter implements ServerAuthenticationConverter {

    private final static Logger log = LoggerFactory.getLogger(ServerTokenAuthenticationConverter.class);
    private final TokenProvider tokenProvider;

    public ServerTokenAuthenticationConverter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Mono<Authentication> convert(ServerWebExchange serverWebExchange) {
        log.debug("convert({} - {})", serverWebExchange.getRequest().getMethod(), serverWebExchange.getRequest().getPath());

        return Mono.justOrEmpty(serverWebExchange)
                .flatMap(swe -> Mono.justOrEmpty(swe.getRequest().getCookies().getFirst("x-auth").getValue()))
                .filter(Objects::nonNull)
                .map(s -> (Authentication)(new UsernamePasswordAuthenticationToken(s, s)))
                .onErrorResume(e -> Mono.fromRunnable(() -> serverWebExchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)));
    }
}

