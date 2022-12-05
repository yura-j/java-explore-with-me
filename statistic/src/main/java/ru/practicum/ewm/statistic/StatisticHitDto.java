package ru.practicum.ewm.statistic;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StatisticHitDto {
    private Integer id;
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
