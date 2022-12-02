package ru.practicum.ewm.user;

import lombok.*;
import ru.practicum.ewm.markers.Base;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String email;
    @NotEmpty(groups = {Base.class})
    private String name;
}
