package cc.kertaskerja.integration.penetapan;

import cc.kertaskerja.integration.penetapan.sasaran_opd.PenetapanSasaranOpd;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class PenetapanSasaranOpdClient {

    private static final Logger log = LoggerFactory.getLogger(PenetapanSasaranOpdClient.class);
    private final WebClient webClient;
    private final PenetapanProperties properties;
    private final ObjectMapper objectMapper;

    public PenetapanSasaranOpdClient(
            WebClient penetapanWebClient,
            PenetapanProperties properties,
            ObjectMapper objectMapper
    ) {
        this.webClient = penetapanWebClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public Mono<List<PenetapanSasaranOpd.SasaranPenetapanData>> fetchSasaranOpd(String kodeOpd, int tahun) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/opd/sasaran")
                        .queryParam("kodeOpd", kodeOpd)
                        .queryParam("tahun", tahun)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseSasaranOpdPayload)
                .onErrorResume(e -> {
                    log.warn("Failed to fetch penetapan sasaran OPD for kodeOpd={}, tahun={}", kodeOpd, tahun, e);
                    return Mono.just(List.of());
                });
    }

    private List<PenetapanSasaranOpd.SasaranPenetapanData> parseSasaranOpdPayload(String payload) {
        try {
            JsonNode rootNode = objectMapper.readTree(payload);
            JsonNode dataNode = rootNode;
            if (rootNode != null && rootNode.isObject() && rootNode.has("data")) {
                dataNode = rootNode.get("data");
            }

            PenetapanSasaranOpd.PenetapanSasaranOpdRoot root = objectMapper.treeToValue(dataNode, PenetapanSasaranOpd.PenetapanSasaranOpdRoot.class);
            if (root == null || root.sasaranOpds() == null) {
                log.warn("PenetapanSasaranOpdRoot or sasaranOpds is null");
                return List.of();
            }

            return root.sasaranOpds().stream()
                    .map(s -> new PenetapanSasaranOpd.SasaranPenetapanData(
                            s.id(),
                            s.kodeSasaranOpd(),
                            s.sasaranOpd(),
                            s.periode(),
                            root.kodeOpd(),
                            root.tahunAktif(),
                            root.versi(),
                            root.isLocked(),
                            s.indikators()
                    ))
                    .toList();
        } catch (Exception e) {
            log.warn("Failed to parse penetapan sasaran OPD payload", e);
            return List.of();
        }
    }
}
