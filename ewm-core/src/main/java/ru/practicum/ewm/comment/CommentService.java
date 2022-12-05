package ru.practicum.ewm.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentsRepository commentsRepository;
    private final EventRepository eventRepository;

    @Transactional
    public CommentOutputDto create(Long eventId, CommentInputDto dto) {
        eventRepository.findById(eventId).orElseThrow(NotFoundException::new);
        Comment comment = CommentMapper.fromDto(dto, eventId, false);

        Comment savedComment = commentsRepository.save(comment);

        return CommentMapper.toDto(savedComment);
    }

    public CommentOutputDto get(Long commentId) {
        Comment comment = commentsRepository.findById(commentId).orElseThrow(NotFoundException::new);

        return CommentMapper.toDto(comment);
    }

    @Transactional
    public void delete(Long userId, Long commentId) {
        Comment comment = commentsRepository
                .findById(commentId).orElseThrow(NotFoundException::new);
        eventRepository.findByIdAndInitiatorId(comment.getEventId(), userId)
                .orElseThrow(NotFoundException::new);

        commentsRepository.delete(comment);
    }

    @Transactional
    public CommentOutputDto pin(Long userId, Long commentId) {
        Comment comment = commentsRepository
                .findById(commentId).orElseThrow(NotFoundException::new);
        eventRepository.findByIdAndInitiatorId(comment.getEventId(), userId)
                .orElseThrow(NotFoundException::new);

        comment.setPinned(true);

        return CommentMapper.toDto(comment);
    }

    @Transactional
    public CommentOutputDto unpin(Long userId, Long commentId) {
        Comment comment = commentsRepository
                .findById(commentId).orElseThrow(NotFoundException::new);
        eventRepository.findByIdAndInitiatorId(comment.getEventId(), userId)
                .orElseThrow(NotFoundException::new);

        comment.setPinned(false);

        return CommentMapper.toDto(comment);
    }
}
