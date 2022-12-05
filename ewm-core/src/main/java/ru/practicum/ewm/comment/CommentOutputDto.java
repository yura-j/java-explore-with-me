package ru.practicum.ewm.comment;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentOutputDto {

    private Long id;
    private String author;
    private Boolean pinned;
    private String text;
    private String dtCreate;
    private String dtUpdate;
    private List<Comment> comments;

    @AllArgsConstructor
    public static class Comment {
        private Long id;
        private String author;
        private String dtCreate;
        private String dtUpdate;
        private String text;
    }

}
