package ru.practicum.ewm.category;

import lombok.*;
import ru.practicum.ewm.markers.Base;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    @NotEmpty(groups = Base.class)
    private String name;
}
