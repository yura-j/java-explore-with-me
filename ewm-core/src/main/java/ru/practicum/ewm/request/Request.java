package ru.practicum.ewm.request;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(value = EnumType.STRING)
    private RequestStatus status;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(
            name = "event_id",
            insertable = false,
            updatable = false)
    private Long eventId;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;

    @CreationTimestamp
    @Column(name = "date_create")
    private LocalDateTime timestamp;
}
