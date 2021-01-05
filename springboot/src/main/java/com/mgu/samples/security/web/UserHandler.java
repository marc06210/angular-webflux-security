package com.mgu.samples.security.web;

import com.mgu.samples.security.security.TokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Component
public class UserHandler {
    private static final Logger log = LoggerFactory.getLogger(UserHandler.class);
    private MapReactiveUserDetailsService userDetailsService;

    private PasswordEncoder encoder;

    private TokenProvider tokenProvider;

    public UserHandler(MapReactiveUserDetailsService userDetailsService, PasswordEncoder encoder, TokenProvider tokenProvider) {
        this.userDetailsService = userDetailsService;
        this.encoder = encoder;
        this.tokenProvider = tokenProvider;
    }

    public Mono<ServerResponse> login(ServerRequest request) {
        // @formatter:off
        return request.bodyToMono(UserCredentials.class)
                .map(uc -> {
                    Mono<UserCredentials> cred = Mono.just(uc);
                    Mono<UserDetails> userInDb = userDetailsService.findByUsername(uc.getUsername());
                    return Mono.zip(userInDb, cred)
                            .filter(t -> encoder.matches(t.getT2().getPassword(), t.getT1().getPassword()))
                            .map(Tuple2::getT1);
                })
                .flatMap(ud -> ud)
                .map(ud -> {
                    log.debug("user identified, setting the cookie");
                    String jwt = tokenProvider.createToken(ud);
                    ResponseCookie cookie = ResponseCookie.fromClientResponse("x-auth", jwt)
                            .maxAge(3600)
                            .httpOnly(true)
                            .path("/")
                            .secure(false)
                            .build();
                    return ServerResponse.noContent().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
                })
                .flatMap(t -> t)
                .switchIfEmpty(ServerResponse.status(HttpStatus.FORBIDDEN).build());
        // @formatter:on
    }
}
