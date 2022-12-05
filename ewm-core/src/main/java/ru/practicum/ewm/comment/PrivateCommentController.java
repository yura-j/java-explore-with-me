package ru.practicum.ewm.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/{userId}/comments")
public class PrivateCommentController {

    private final CommentService commentService;

    @DeleteMapping("/{commentId}")
    void delete(
            @PathVariable Long userId,
            @PathVariable Long commentId
    ) {
        log.info("Пользователь {} удаляет коммент {}", userId, commentId);

        commentService.delete(userId, commentId);
    }

    @PatchMapping("/{commentId}/pin")
    CommentOutputDto pin(
            @PathVariable Long userId,
            @PathVariable Long commentId
    ) {
        log.info("Пользователь {} закрепляет комментарий {}", userId, commentId);

        return commentService.pin(userId, commentId);
    }

    @PatchMapping("/{commentId}/unpin")
    CommentOutputDto unpin(
            @PathVariable Long userId,
            @PathVariable Long commentId
    ) {
        log.info("Пользователь {} открепляет комментарий {}", userId, commentId);

        return commentService.unpin(userId, commentId);
    }
}
