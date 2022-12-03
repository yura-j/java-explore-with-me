package ru.practicum.ewm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService service;

    @GetMapping("/stats")
    public List<StatisticDto> get(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(defaultValue = "false", required = false) Boolean unique,
            @RequestParam(required = false) List<String> uris
    ) {
        log.info("get stats, start:{}, end:{}", start, end);
        List<String> urisDecoded = null == uris
                ? List.of()
                : uris
                .stream()
                .map(uri -> UriUtils.decode(uri, StandardCharsets.UTF_8))
                .collect(Collectors.toList());

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(start, df);
        LocalDateTime endTime = LocalDateTime.parse(end, df);

        return service.getStatistic(startTime, endTime, unique, urisDecoded);
    }

    @PostMapping("/hit")
    public StatisticHitDto save(
            @RequestBody StatisticHitDto dto
    ) {
        log.info("save hit {}", dto);

        return service.saveStatistic(dto);
    }
}
