package cc.kertaskerja.integration.penetapan;

import cc.kertaskerja.integration.penetapan.renja.PenetapanRenjaOpd;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class PenetapanRenjaOpdClient {

    private static final Logger log = LoggerFactory.getLogger(PenetapanRenjaOpdClient.class);
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public PenetapanRenjaOpdClient(
            WebClient penetapanWebClient,
            ObjectMapper objectMapper
    ) {
        this.webClient = penetapanWebClient;
        this.objectMapper = objectMapper;
    }

    public Mono<PenetapanRenjaOpd.PenetapanRenjaOpdRoot> fetchRenjaOpd(String kodeOpd, int tahun) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/opd/renja")
                        .queryParam("kodeOpd", kodeOpd)
                        .queryParam("tahun", tahun)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseRenjaOpdPayload)
                .onErrorResume(e -> {
                    log.warn("Failed to fetch penetapan renja OPD for kodeOpd={}, tahun={}", kodeOpd, tahun, e);
                    return Mono.empty();
                });
    }

    public Mono<String> syncRenjaOpd(String kodeOpd, int tahun) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/opd/renja/sync")
                        .queryParam("kodeOpd", kodeOpd)
                        .queryParam("tahun", tahun)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    log.error("Failed to sync penetapan renja OPD for kodeOpd={}, tahun={}", kodeOpd, tahun, e);
                    return Mono.empty();
                });
    }

    private PenetapanRenjaOpd.PenetapanRenjaOpdRoot parseRenjaOpdPayload(String payload) {
        try {
            JsonNode rootNode = objectMapper.readTree(payload);
            JsonNode dataNode = rootNode;
            if (rootNode != null && rootNode.isObject() && rootNode.has("data")) {
                dataNode = rootNode.get("data");
            }

            return objectMapper.treeToValue(dataNode, PenetapanRenjaOpd.PenetapanRenjaOpdRoot.class);
        } catch (Exception e) {
            log.warn("Failed to parse penetapan renja OPD payload", e);
            return null;
        }
    }
}
