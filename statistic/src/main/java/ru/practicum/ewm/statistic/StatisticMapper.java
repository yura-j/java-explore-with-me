package ru.practicum.ewm.statistic;

public class StatisticMapper {
    public static StatisticHitModel fromDto(StatisticHitDto dto) {

        return StatisticHitModel.builder()
                .app(dto.getApp())
                .uri(dto.getUri())
                .ip(dto.getIp())
                .build();
    }

    public static StatisticHitDto toDto(StatisticHitModel updatedStatistic) {

        return StatisticHitDto.builder()
                .id(updatedStatistic.getId())
                .app(updatedStatistic.getApp())
                .uri(updatedStatistic.getUri())
                .ip(updatedStatistic.getIp())
                .timestamp(updatedStatistic.getTimestamp().toString())
                .build();
    }

    public static StatisticDto toStatisticDto(StatisticModel statisticModel) {

        return StatisticDto.builder()
                .app(statisticModel.getApp())
                .uri(statisticModel.getUri())
                .hits(statisticModel.getHits())
                .build();
    }
}
