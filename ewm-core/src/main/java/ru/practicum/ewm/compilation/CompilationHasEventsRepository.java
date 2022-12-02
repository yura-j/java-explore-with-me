package ru.practicum.ewm.compilation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompilationHasEventsRepository extends JpaRepository<CompilationHasEvents, Long> {
    List<CompilationHasEvents> findAllByCompilationIdIn(List<Long> compilationIds);
    void deleteByCompilationIdAndEventId(Long compilationId, Long eventId);
}
