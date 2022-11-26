package ru.practicum.ewm;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticService {

    private final StatisticRepository statisticRepository;

    public List<StatisticDto> getStatistic(LocalDateTime startTime, LocalDateTime endTime, Boolean unique, List<String> uris) {
        List<StatisticModel> statistics = new ArrayList<>();
        if (unique && null != uris) {
            statistics = statisticRepository.countUniqueHitsByStartTimeAndEndTimeAndUrisIn(startTime, endTime, uris);
        }
        if (unique && null == uris) {
            statistics = statisticRepository.countUniqueHitsByStartTimeAndEndTime(startTime, endTime);
        }

        if (!unique && null != uris) {
            statistics = statisticRepository.countHitsByStartTimeAndEndTimeAndUrisIn(startTime, endTime, uris);
        }
        if (!unique && null == uris) {
            statistics = statisticRepository.countHitsByStartTimeAndEndTime(startTime, endTime);
        }

        return statistics
                .stream()
                .map(StatisticMapper::toStatisticDto)
                .collect(Collectors.toList());
    }


    @Transactional
    public StatisticHitDto saveStatistic(StatisticHitDto dto) {

        StatisticHitModel statistic = StatisticMapper.fromDto(dto);
        StatisticHitModel updatedStatistic = statisticRepository.save(statistic);
        return StatisticMapper.toDto(updatedStatistic);
    }
}
