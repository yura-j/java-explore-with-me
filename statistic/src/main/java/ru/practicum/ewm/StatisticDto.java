package ru.practicum.ewm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class StatisticDto {
    private String app;
    private String uri;
    private Long hits;
}
