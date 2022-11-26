package ru.practicum.ewm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
        log.debug("get stats, start:{}, end:{}", start, end);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(start, df);
        LocalDateTime endTime = LocalDateTime.parse(end, df);
        return service.getStatistic(startTime, endTime, unique, uris);
    }

    @PostMapping("/hit")
    public StatisticHitDto save(
        @RequestBody StatisticHitDto dto
    ) {
        log.debug("save hit {}", dto);
        return service.saveStatistic(dto);
    }
}
