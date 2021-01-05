package com.mgu.samples.security.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Configuration
@EnableWebFluxSecurity
public class CustomSecurity {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private ServerTokenAuthenticationConverter authConverter;

    @Autowired
    private ServerTokenAuthenticationManager authMgr;

    private static String[] permittedUrl = new String[]{ "/login", "/public/**" };

    @Bean
    public SecurityWebFilterChain securitygWebFilterChain(
            ServerHttpSecurity http) {
        return http.authorizeExchange()
                .pathMatchers(HttpMethod.POST, "/login").permitAll() // the login POST must be accessible to all
                .pathMatchers("/public/**").permitAll() // the /public/** must be accessible to all
                .anyExchange().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(new SecurityEntryPoint()) // this line to control what is returned when no auth is found
                .and()
                .addFilterAt(piPhotoAuthenticationWebFiler(), SecurityWebFiltersOrder.AUTHENTICATION) // this is our auth filter
                .csrf().disable() // stateless, no csrf
                .httpBasic().disable() // no http basic, we handle our own /login
                .formLogin().disable() // no form login, we handle our own /login and the ui is done on the frontend
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) // stateless, no security context
                .build();
    }

    public AuthenticationWebFilter piPhotoAuthenticationWebFiler() {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(authMgr);
        filter.setServerAuthenticationConverter(authConverter);
        filter.setRequiresAuthenticationMatcher(
                new NegatedServerWebExchangeMatcher(ServerWebExchangeMatchers.pathMatchers(permittedUrl)));
        return filter;
    }

    @Bean
    public PasswordEncoder encoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        PasswordEncoder encoder = encoder();
        UserDetails user = User
                .withUsername("user")
                .password(encoder.encode("pwd"))
                .roles("USER")
                .build();
        return new MapReactiveUserDetailsService(user);
    }
}
