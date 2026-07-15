package cc.kertaskerja.integration.kepegawaian;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class PegawaiClient {

    private static final Logger log = LoggerFactory.getLogger(PegawaiClient.class);
    private final WebClient webClient;

    public PegawaiClient(WebClient.Builder webClientBuilder,
                         @Value("${integration.kepegawaian.base-url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public record PegawaiData(
            Integer id,
            String nip,
            @JsonProperty("nama_pegawai") String namaPegawai,
            @JsonProperty("status_pegawai") String statusPegawai
    ) {}

    public record PegawaiResponse(
            Integer code,
            String status,
            String message,
            List<PegawaiData> data
    ) {}

    public Mono<List<PegawaiData>> fetchAllPegawai() {
        return webClient.get()
                .uri("/pegawai")
                .retrieve()
                .bodyToMono(PegawaiResponse.class)
                .map(response -> response.data() != null ? response.data() : List.<PegawaiData>of())
                .onErrorResume(e -> {
                    log.warn("Failed to fetch pegawai list from kepegawaian service", e);
                    return Mono.just(List.<PegawaiData>of());
                });
    }
}
