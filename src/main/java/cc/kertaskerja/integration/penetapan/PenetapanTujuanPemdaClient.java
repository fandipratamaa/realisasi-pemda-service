package cc.kertaskerja.integration.penetapan;

import cc.kertaskerja.integration.penetapan.tujuan_pemda.PenetapanTujuanPemda;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class PenetapanTujuanPemdaClient {

    private static final Logger log = LoggerFactory.getLogger(PenetapanTujuanPemdaClient.class);
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public PenetapanTujuanPemdaClient(
            WebClient penetapanWebClient,
            ObjectMapper objectMapper
    ) {
        this.webClient = penetapanWebClient;
        this.objectMapper = objectMapper;
    }

    public Mono<List<PenetapanTujuanPemda.TujuanPenetapanPemdaData>> fetchTujuanPemda(int tahun) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/pemda/tujuan")
                        .queryParam("tahun", tahun)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseTujuanPemdaPayload)
                .onErrorResume(e -> {
                    log.warn("Failed to fetch penetapan tujuan pemda for tahun={}", tahun, e);
                    return Mono.just(List.of());
                });
    }

    public Mono<String> syncTujuanPemda(int tahun) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/pemda/tujuan/sync")
                        .queryParam("tahun", tahun)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> log.error("Failed to sync penetapan tujuan pemda for tahun={}", tahun, e));
    }

    private List<PenetapanTujuanPemda.TujuanPenetapanPemdaData> parseTujuanPemdaPayload(String payload) {
        try {
            JsonNode rootNode = objectMapper.readTree(payload);
            JsonNode dataNode = rootNode;
            if (rootNode != null && rootNode.isObject() && rootNode.has("data")) {
                dataNode = rootNode.get("data");
            }

            PenetapanTujuanPemda.PenetapanTujuanPemdaRoot root = objectMapper.treeToValue(dataNode, PenetapanTujuanPemda.PenetapanTujuanPemdaRoot.class);
            if (root == null || root.tujuanPemdas() == null) {
                log.warn("PenetapanTujuanPemdaRoot or tujuanPemdas is null");
                return List.of();
            }

            return root.tujuanPemdas().stream()
                    .map(t -> new PenetapanTujuanPemda.TujuanPenetapanPemdaData(
                            t.id(),
                            t.visi(),
                            t.misi(),
                            t.kodeTujuanPemda(),
                            t.tujuanPemda(),
                            t.periode(),
                            root.tahunAktif(),
                            root.versi(),
                            root.isLocked(),
                            t.indikators()
                    ))
                    .toList();
        } catch (Exception e) {
            log.warn("Failed to parse penetapan tujuan pemda payload", e);
            return List.of();
        }
    }
}
