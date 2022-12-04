package ru.practicum.ewm.compilation;

import lombok.*;
import ru.practicum.ewm.marker.Base;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompilationInputDto {
    private List<Long> events;
    private Boolean pinned;
    @NotEmpty(groups = Base.class)
    private String title;
}
