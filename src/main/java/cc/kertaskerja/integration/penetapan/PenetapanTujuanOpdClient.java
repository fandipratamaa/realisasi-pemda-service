package cc.kertaskerja.integration.penetapan;

import cc.kertaskerja.integration.penetapan.tujuan_opd.PenetapanTujuanOpd;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class PenetapanTujuanOpdClient {

    private static final Logger log = LoggerFactory.getLogger(PenetapanTujuanOpdClient.class);
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public PenetapanTujuanOpdClient(
            WebClient penetapanWebClient,
            ObjectMapper objectMapper
    ) {
        this.webClient = penetapanWebClient;
        this.objectMapper = objectMapper;
    }

    public Mono<List<PenetapanTujuanOpd.TujuanPenetapanData>> fetchTujuanOpd(String kodeOpd, int tahun) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/opd/tujuan")
                        .queryParam("kodeOpd", kodeOpd)
                        .queryParam("tahun", tahun)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseTujuanOpdPayload)
                .onErrorResume(e -> {
                    log.warn("Failed to fetch penetapan tujuan OPD for kodeOpd={}, tahun={}", kodeOpd, tahun, e);
                    return Mono.just(List.of());
                });
    }

    public Mono<String> syncTujuanOpd(String kodeOpd, int tahun) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/opd/tujuan/sync")
                        .queryParam("kodeOpd", kodeOpd)
                        .queryParam("tahun", tahun)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    log.error("Failed to sync penetapan tujuan OPD for kodeOpd={}, tahun={}", kodeOpd, tahun, e);
                    return Mono.empty();
                });
    }

    private List<PenetapanTujuanOpd.TujuanPenetapanData> parseTujuanOpdPayload(String payload) {
        try {
            JsonNode rootNode = objectMapper.readTree(payload);
            JsonNode dataNode = rootNode;
            if (rootNode != null && rootNode.isObject() && rootNode.has("data")) {
                dataNode = rootNode.get("data");
            }

            PenetapanTujuanOpd.PenetapanTujuanOpdRoot root = objectMapper.treeToValue(dataNode, PenetapanTujuanOpd.PenetapanTujuanOpdRoot.class);
            if (root == null || root.tujuanOpds() == null) {
                log.warn("PenetapanTujuanOpdRoot or tujuanOpds is null");
                return List.of();
            }

            return root.tujuanOpds().stream()
                    .map(t -> new PenetapanTujuanOpd.TujuanPenetapanData(
                            t.id(),
                            t.kodeTujuanOpd(),
                            t.tujuanOpd(),
                            t.periode(),
                            root.kodeOpd(),
                            root.tahunAktif(),
                            root.versi(),
                            root.isLocked(),
                            t.indikators()
                    ))
                    .toList();
        } catch (Exception e) {
            log.warn("Failed to parse penetapan tujuan OPD payload", e);
            return List.of();
        }
    }
}
