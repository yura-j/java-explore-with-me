package ru.practicum.ewm.comment;

import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {

    public static Comment fromDto(CommentInputDto dto, Long eventId, Boolean pinned) {
        return Comment
                .builder()
                .author(dto.getAuthor())
                .parentId(dto.getParentId())
                .text(dto.getText())
                .eventId(eventId)
                .pinned(pinned)
                .build();
    }

    public static CommentOutputDto toDto(Comment savedComment) {
        return CommentOutputDto
                .builder()
                .comments(null == savedComment.getComments()
                        ? List.of()
                        : savedComment.getComments()
                        .stream()
                        .map(c -> new CommentOutputDto.Comment(
                                c.getId(),
                                c.getAuthor(),
                                c.getDtUpdate().toString(),
                                c.getDtCreate().toString(),
                                c.getText()
                        )).collect(Collectors.toList())
                )
                .author(savedComment.getAuthor())
                .text(savedComment.getText())
                .dtCreate(savedComment.getDtCreate().toString())
                .dtUpdate(savedComment.getDtUpdate().toString())
                .pinned(savedComment.getPinned())
                .build();
    }
}
