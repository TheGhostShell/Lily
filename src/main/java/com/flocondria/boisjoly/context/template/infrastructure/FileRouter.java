package com.flocondria.boisjoly.context.template.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class FileRouter {
    @Bean
    public RouterFunction<ServerResponse> fileRoute(FileHandler handler){

        return RouterFunctions.route(RequestPredicates.POST("/upload"), handler::upload)
                .and(RouterFunctions.resources("/**", new ClassPathResource("static/")));
    }
}
