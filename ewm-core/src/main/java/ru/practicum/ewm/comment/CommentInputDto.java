package ru.practicum.ewm.comment;

import lombok.*;
import ru.practicum.ewm.util.marker.Base;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentInputDto {

    @NotNull(groups = Base.class)
    private String author;
    @NotNull(groups = Base.class)
    private String text;
    private Long parentId;

}
