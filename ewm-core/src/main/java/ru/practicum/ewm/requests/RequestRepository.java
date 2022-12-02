package ru.practicum.ewm.requests;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByEventIdAndStatus(Long eventId, RequestStatus status);

    List<Request> findAllByRequesterId(Long userId);

    Optional<Request> findByRequesterIdAndEventId(Long userId, Long eventId);

    Optional<Request> findByIdAndRequesterId(Long requestId, Long userId);

    @Query("select count(r) from Request r where r.event.id = :eventId and r.status = ':status'")
    Integer countByIdAndStatus(Long eventId, RequestStatus status);

    List<Request> findAllByStatusAndEventIdIn(RequestStatus confirmed, List<Long> eventIds);

    Optional<Request> findByIdAndEventId(Long requestId, Long id);
}
