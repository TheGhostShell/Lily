package com.flocondria.boisjoly.context.template.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.Map;

@Configuration
public class FileHandler {

    @Value("#{systemProperties['user.dir']}")
    private String dir;

    public Mono<ServerResponse> upload(ServerRequest serverRequest) {
        return serverRequest
                .body(BodyExtractors.toMultipartData())
                .flatMap(part -> {
                    Map<String, Part> map = part.toSingleValueMap();
                    FilePart filepart = (FilePart) map.get("files");
                    return filepart
                            .transferTo(new File(dir + "/" + filepart.filename()).toPath())
                            .then(ServerResponse.ok().body(BodyInserters.fromObject("Ok")));
                });
    }
}
