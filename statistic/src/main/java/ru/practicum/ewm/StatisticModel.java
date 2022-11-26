package ru.practicum.ewm;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticModel {

    private String app;
    private String uri;
    private Long hits;
}
