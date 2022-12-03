package ru.practicum.ewm.statistic.client;

import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class StatisticHitDto {
    private Integer id;
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
