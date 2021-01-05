package com.mgu.samples.security.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ServerTokenAuthenticationManager implements ReactiveAuthenticationManager {

    private static final Logger log = LoggerFactory.getLogger(ServerTokenAuthenticationManager.class);
    private TokenProvider tokenProvider;

    public ServerTokenAuthenticationManager(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Mono<Authentication> authenticate(final Authentication authentication) {
        log.debug("authenticate({})", authentication);

        if (authentication.isAuthenticated()) {
            log.debug("authentication was already processed");
            return Mono.just(authentication);
        }
        log.debug("authenticating needed");
        return Mono.just(authentication)
                .map(Authentication::getCredentials)
                .cast(String.class)
                .map(s -> tokenProvider.getAuthentication(s))
                .onErrorResume(e -> Mono.error(new BadCredentialsException("Invalid Credentials")));
    }
}
