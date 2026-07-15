package cc.kertaskerja.integration.upload;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class UploadClient {

    private static final Logger log = LoggerFactory.getLogger(UploadClient.class);
    private final WebClient webClient;

    public UploadClient(WebClient.Builder webClientBuilder,
                        @Value("${integration.upload.base-url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public record UploadMetadata(
            Integer id,
            @JsonProperty("object_key") String objectKey,
            String bucket,
            @JsonProperty("original_name") String originalName,
            String extension,
            @JsonProperty("content_type") String contentType,
            @JsonProperty("file_size") Long fileSize,
            @JsonProperty("checksum_algorithm") String checksumAlgorithm,
            String checksum,
            String category,
            String visibility,
            String url
    ) {}

    public Mono<UploadMetadata> uploadFile(FilePart filePart) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.asyncPart("file", filePart.content(), org.springframework.core.io.buffer.DataBuffer.class)
                .filename(filePart.filename());

        return webClient.post()
                .uri("/upload")
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(UploadMetadata.class)
                .doOnError(e -> log.error("Failed to upload file to external service", e));
    }

    public Flux<UploadMetadata> getFiles() {
        return webClient.get()
                .uri("/files")
                .retrieve()
                .bodyToFlux(UploadMetadata.class)
                .doOnError(e -> log.error("Failed to get files from external service", e));
    }
}
