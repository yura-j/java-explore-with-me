package ru.practicum.ewm.statistic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticRepository extends JpaRepository<StatisticHitModel, Long> {

    @Query("SELECT new ru.practicum.ewm.statistic.StatisticModel(MAX(s.app), s.uri, COUNT(DISTINCT s.ip)) "
            + "FROM StatisticHitModel AS s where s.uri in :uris AND s.timestamp BETWEEN :startTime AND :endTime  GROUP BY s.uri")
    List<StatisticModel> countUniqueHitsByStartTimeAndEndTimeAndUrisIn(LocalDateTime startTime, LocalDateTime endTime, List<String> uris);

    @Query("SELECT new ru.practicum.ewm.statistic.StatisticModel(MAX(s.app), s.uri, COUNT(DISTINCT s.ip)) "
            + "FROM StatisticHitModel AS s where s.timestamp BETWEEN :startTime AND :endTime  GROUP BY s.uri")
    List<StatisticModel> countUniqueHitsByStartTimeAndEndTime(LocalDateTime startTime, LocalDateTime endTime);

    @Query("SELECT new ru.practicum.ewm.statistic.StatisticModel(MAX(s.app), s.uri, COUNT(s.ip)) "
            + "FROM StatisticHitModel AS s where s.uri in :uris AND s.timestamp BETWEEN :startTime AND :endTime  GROUP BY s.uri")
    List<StatisticModel> countHitsByStartTimeAndEndTimeAndUrisIn(LocalDateTime startTime, LocalDateTime endTime, List<String> uris);

    @Query("SELECT new ru.practicum.ewm.statistic.StatisticModel(MAX(s.app), s.uri, COUNT(s.ip)) "
            + "FROM StatisticHitModel AS s where s.timestamp BETWEEN :startTime AND :endTime  GROUP BY s.uri")
    List<StatisticModel> countHitsByStartTimeAndEndTime(LocalDateTime startTime, LocalDateTime endTime);
}
