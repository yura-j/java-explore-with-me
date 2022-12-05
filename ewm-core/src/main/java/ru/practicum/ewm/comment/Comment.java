package ru.practicum.ewm.comment;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String author;

    private Boolean pinned;

    @Column(name = "comment_text")
    private String text;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "parent_id")
    private Long parentId;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "parent_id")
    private List<Comment> comments;

    @Column(name = "date_create")
    @CreationTimestamp
    private LocalDateTime dtCreate;
    @Column(name = "date_update")
    @UpdateTimestamp
    private LocalDateTime dtUpdate;
}
