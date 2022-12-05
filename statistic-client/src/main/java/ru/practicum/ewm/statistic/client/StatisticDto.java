package ru.practicum.ewm.statistic.client;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class StatisticDto {
    private String app;
    private String uri;
    private Long hits;
}
