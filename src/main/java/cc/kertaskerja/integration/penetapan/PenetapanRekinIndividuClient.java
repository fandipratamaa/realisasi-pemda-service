package cc.kertaskerja.integration.penetapan;

import cc.kertaskerja.integration.penetapan.rekin.PenetapanRekinIndividu;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class PenetapanRekinIndividuClient {

    private static final Logger log = LoggerFactory.getLogger(PenetapanRekinIndividuClient.class);
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public PenetapanRekinIndividuClient(
            WebClient penetapanWebClient,
            ObjectMapper objectMapper
    ) {
        this.webClient = penetapanWebClient;
        this.objectMapper = objectMapper;
    }

    public Mono<PenetapanRekinIndividu.RekinIndividuData> fetchRekinIndividu(String pegawaiId, String kodeOpd, int tahun) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/individu/rekin")
                        .queryParam("pegawaiId", pegawaiId)
                        .queryParam("kodeOpd", kodeOpd)
                        .queryParam("tahun", tahun)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseRekinIndividuPayload)
                .onErrorResume(e -> {
                    log.warn("Failed to fetch rekin individu for pegawaiId={}, kodeOpd={}, tahun={}", pegawaiId, kodeOpd, tahun, e);
                    return Mono.just(new PenetapanRekinIndividu.RekinIndividuData(
                            pegawaiId, null, kodeOpd, tahun, List.of()
                    ));
                });
    }

    private PenetapanRekinIndividu.RekinIndividuData parseRekinIndividuPayload(String payload) {
        try {
            JsonNode rootNode = objectMapper.readTree(payload);
            JsonNode dataNode = rootNode;
            if (rootNode != null && rootNode.isObject() && rootNode.has("data")) {
                dataNode = rootNode.get("data");
            }

            PenetapanRekinIndividu.RekinIndividuData data = objectMapper.treeToValue(dataNode, PenetapanRekinIndividu.RekinIndividuData.class);
            if (data == null || data.rekins() == null) {
                log.warn("RekinIndividuData or rekins is null");
                return null;
            }

            return data;
        } catch (Exception e) {
            log.warn("Failed to parse rekin individu payload", e);
            return null;
        }
    }
}