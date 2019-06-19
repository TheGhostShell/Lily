package com.flocondria.boisjoly.context.template.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.apache.commons.io.FileUtils;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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
                    Part filepart = map.get("files");

                    try {
                        File realFile = new File("./uploaded/" + filepart.headers().getContentDisposition().getFilename());
                        FileUtils.touch(realFile);
                        WritableByteChannel channel = Files.newByteChannel(realFile.toPath(), StandardOpenOption.WRITE);

                        return DataBufferUtils.write(filepart.content(), channel)
                                .map(DataBufferUtils::release)
                                .doOnTerminate(() -> {
                                    try {
                                        channel.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                })
                                .then(ServerResponse.ok().body(BodyInserters.fromObject("Ok")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return ServerResponse.ok().body(BodyInserters.fromObject("Not Ok"));
                });
    }
}
