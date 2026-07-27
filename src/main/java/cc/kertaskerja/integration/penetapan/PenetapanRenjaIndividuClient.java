package cc.kertaskerja.integration.penetapan;

import cc.kertaskerja.integration.penetapan.renja.PenetapanRenjaIndividu;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class PenetapanRenjaIndividuClient {

    private static final Logger log = LoggerFactory.getLogger(PenetapanRenjaIndividuClient.class);
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public PenetapanRenjaIndividuClient(
            WebClient penetapanWebClient,
            ObjectMapper objectMapper
    ) {
        this.webClient = penetapanWebClient;
        this.objectMapper = objectMapper;
    }

    public Mono<PenetapanRenjaIndividu.RenjaIndividuData> fetchRenjaIndividu(String pegawaiId, String kodeOpd, int tahun) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/individu/renja")
                        .queryParam("pegawaiId", pegawaiId)
                        .queryParam("kodeOpd", kodeOpd)
                        .queryParam("tahun", tahun)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseRenjaIndividuPayload)
                .onErrorResume(e -> {
                    log.warn("Failed to fetch renja individu for pegawaiId={}, kodeOpd={}, tahun={}", pegawaiId, kodeOpd, tahun, e);
                    return Mono.just(new PenetapanRenjaIndividu.RenjaIndividuData(
                            pegawaiId, null, kodeOpd, tahun, List.of()
                    ));
                });
    }

    public Mono<String> syncRenjaIndividu(String pegawaiId, String kodeOpd, int tahun) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/individu/renja/sync")
                        .queryParam("pegawaiId", pegawaiId)
                        .queryParam("kodeOpd", kodeOpd)
                        .queryParam("tahun", tahun)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    log.error("Failed to sync renja individu for pegawaiId={}, kodeOpd={}, tahun={}", pegawaiId, kodeOpd, tahun, e);
                    return Mono.empty();
                });
    }

    private PenetapanRenjaIndividu.RenjaIndividuData parseRenjaIndividuPayload(String payload) {
        try {
            JsonNode rootNode = objectMapper.readTree(payload);
            JsonNode dataNode = rootNode;
            if (rootNode != null && rootNode.isObject() && rootNode.has("data")) {
                dataNode = rootNode.get("data");
            }

            PenetapanRenjaIndividu.RenjaIndividuData data = objectMapper.treeToValue(dataNode, PenetapanRenjaIndividu.RenjaIndividuData.class);
            if (data == null || data.renjas() == null) {
                log.warn("RenjaIndividuData or renjas is null");
                return null;
            }

            return data;
        } catch (Exception e) {
            log.warn("Failed to parse renja individu payload", e);
            return null;
        }
    }
}
