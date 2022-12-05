package ru.practicum.ewm.event;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Where;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.comment.Comment;
import ru.practicum.ewm.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String annotation;

    @Column(nullable = false, length = 512)
    @Enumerated(value = EnumType.STRING)
    private EventState state;

    @Column(nullable = false, length = 512)
    private String title;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    private LocalDateTime publishedOn;

    @Column(nullable = false)
    private Float lat;
    @Column(nullable = false)
    private Float lon;
    @Column(nullable = false)
    private Boolean paid;
    @Column(nullable = false)
    private Integer participantLimit;
    @Column(nullable = false)
    private Boolean requestModeration;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "event_id")
    @Where(clause = "parent_id IS NULL")
    private List<Comment> comments;

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
