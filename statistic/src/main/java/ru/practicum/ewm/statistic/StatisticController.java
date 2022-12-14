package ru.practicum.ewm.statistic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;
import ru.practicum.ewm.statistic.util.DTFormat;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        List<String> urisDecoded = new ArrayList<>();
        if (null != uris) {
            urisDecoded = uris
                    .stream()
                    .map(uri -> UriUtils.decode(uri, StandardCharsets.UTF_8))
                    .collect(Collectors.toList());
        }

        LocalDateTime startTime = LocalDateTime.parse(start, DTFormat.format);
        LocalDateTime endTime = LocalDateTime.parse(end, DTFormat.format);

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
