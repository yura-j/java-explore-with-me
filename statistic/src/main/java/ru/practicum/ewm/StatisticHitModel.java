package ru.practicum.ewm;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "statistics")
public class StatisticHitModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String app;
    private String uri;
    private String ip;

    @CreationTimestamp
    @Column(name = "date_create")
    private LocalDateTime timestamp;
}
