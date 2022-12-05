package ru.practicum.ewm.controller.public_api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.CommentInputDto;
import ru.practicum.ewm.comment.CommentOutputDto;
import ru.practicum.ewm.comment.CommentService;
import ru.practicum.ewm.util.marker.Base;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class PublicCommentController {

    private final CommentService commentService;

    @PostMapping("/events/{eventId}")
    public CommentOutputDto create(
            @PathVariable Long eventId,
            @RequestBody @Validated(Base.class) CommentInputDto dto
    ) {
        log.info("Создается комментарий от пользователя {}", dto.getAuthor());

        return commentService.create(eventId, dto);
    }

    @GetMapping("/{commentId}")
    public CommentOutputDto get(
            @PathVariable Long commentId
    ) {
        log.info("Берется комментарий {}", commentId);

        return commentService.get(commentId);
    }

}
