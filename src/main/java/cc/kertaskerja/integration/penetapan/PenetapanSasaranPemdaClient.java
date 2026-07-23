package cc.kertaskerja.integration.penetapan;

import cc.kertaskerja.integration.penetapan.sasaran_pemda.PenetapanSasaranPemda;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class PenetapanSasaranPemdaClient {

    private static final Logger log = LoggerFactory.getLogger(PenetapanSasaranPemdaClient.class);
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public PenetapanSasaranPemdaClient(
            WebClient penetapanWebClient,
            ObjectMapper objectMapper
    ) {
        this.webClient = penetapanWebClient;
        this.objectMapper = objectMapper;
    }

    public Mono<List<PenetapanSasaranPemda.SasaranPenetapanPemdaData>> fetchSasaranPemda(int tahun) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/pemda/sasaran")
                        .queryParam("tahun", tahun)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseSasaranPemdaPayload)
                .onErrorResume(e -> {
                    log.warn("Failed to fetch penetapan sasaran pemda for tahun={}", tahun, e);
                    return Mono.just(List.of());
                });
    }

    public Mono<String> syncSasaranPemda(int tahun) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/pemda/sasaran/sync")
                        .queryParam("tahun", tahun)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> log.error("Failed to sync penetapan sasaran pemda for tahun={}", tahun, e));
    }

    private List<PenetapanSasaranPemda.SasaranPenetapanPemdaData> parseSasaranPemdaPayload(String payload) {
        try {
            JsonNode rootNode = objectMapper.readTree(payload);
            JsonNode dataNode = rootNode;
            if (rootNode != null && rootNode.isObject() && rootNode.has("data")) {
                dataNode = rootNode.get("data");
            }

            PenetapanSasaranPemda.PenetapanSasaranPemdaRoot root = objectMapper.treeToValue(dataNode, PenetapanSasaranPemda.PenetapanSasaranPemdaRoot.class);
            if (root == null || root.sasaranPemdas() == null) {
                log.warn("PenetapanSasaranPemdaRoot or sasaranPemdas is null");
                return List.of();
            }

            return root.sasaranPemdas().stream()
                    .map(s -> new PenetapanSasaranPemda.SasaranPenetapanPemdaData(
                            s.id(),
                            s.kodeSasaranPemda(),
                            s.sasaranPemda(),
                            s.periode(),
                            root.tahunAktif(),
                            root.versi(),
                            root.isLocked(),
                            s.indikators()
                    ))
                    .toList();
        } catch (Exception e) {
            log.warn("Failed to parse penetapan sasaran pemda payload", e);
            return List.of();
        }
    }
}
