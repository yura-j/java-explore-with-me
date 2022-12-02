package ru.practicum.ewm.event;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private String annotation;
    @Enumerated(value = EnumType.STRING)
    private EventState state;
    private String title;
    private LocalDateTime eventDate;
    private LocalDateTime publishedOn;
    private Float lat;
    private Float lon;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @CreationTimestamp
    @Column(name = "date_create")
    private LocalDateTime timestamp;
}
