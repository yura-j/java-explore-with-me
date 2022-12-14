package ru.practicum.ewm.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestDto {
    private String created;
    private String status;
    private Long event;
    private Long id;
    private Long requester;
}
