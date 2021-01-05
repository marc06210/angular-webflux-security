package com.mgu.samples.security.web;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Component
public class ApplicationRouter {
    @Bean
    public RouterFunction<ServerResponse> userRouter(DataHandler dh) {
        return RouterFunctions
                .route(RequestPredicates.GET("/public/data"), dh::getData)
                .andRoute(RequestPredicates.GET("/data"), dh::getData);
    }
}
