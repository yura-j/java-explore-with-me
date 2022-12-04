package ru.practicum.ewm.statistic.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriUtils;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticService {

    private static final String APP = "EWM";
    private final WebClient wc;

    public void saveHit(String uri, String ip) {
        try {
            wc.post()
                    .uri("/hit")
                    .body(Mono.just(StatisticHitDto
                                    .builder()
                                    .app(APP)
                                    .uri(uri)
                                    .ip(ip)
                                    .build()),
                            StatisticHitDto.class
                    )
                    .retrieve()
                    .bodyToMono(StatisticHitDto.class)
                    .block();
        } catch (Throwable e) {
            log.info("Ошибка при отправке данных на сервер статистики", e);
        }
    }

    public Map<Long, Integer> getEventViews(List<Long> eventIds, String start) {
        Map<Long, Integer> eventViews = new HashMap<>();
        DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<String> uris = eventIds
                .stream()
                .map(id -> UriUtils.encode("/events/" + id, StandardCharsets.UTF_8))
                .collect(Collectors.toList());
        try {
            StatisticDto[] statistic =
                    wc.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/stats")
                                    .queryParam("start", start)
                                    .queryParam("end", LocalDateTime.now().plusHours(1).format(dtFormat))
                                    .queryParam("uris", String.join(",", uris))
                                    .build())
                            .retrieve()
                            .bodyToMono(StatisticDto[].class)
                            .block();
            if (null != statistic) {
                for (StatisticDto dto : statistic) {
                    String uri = dto.getUri();
                    Long id = getEventIdFromEventUri(uri);
                    eventViews.put(id, Math.toIntExact(dto.getHits()));
                }
            }
        } catch (Throwable e) {
            log.info("Ошибка при получении данных с сервера статистики", e);
        }

        return eventViews;
    }

    private Long getEventIdFromEventUri(String uri) {
        String idPart = uri.substring(Math.min(uri.lastIndexOf("/") + 1, uri.length()));
        return Long.parseLong(idPart);
    }
}
