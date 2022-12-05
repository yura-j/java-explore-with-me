package ru.practicum.ewm.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
class CommentsRepositoryTest {

    @Autowired
    private CommentsRepository commentsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
        User owner = userRepository.save(new User(null, "valera", "valera@valera.valera"));
        Category category = categoryRepository.save(Category.builder().name("demonstrations").build());
        Event event = eventRepository.save(Event
                .builder()
                .state(EventState.PUBLISHED)
                .eventDate(LocalDateTime.now().plusDays(3))
                .paid(false)
                .title("ev")
                .lat(10.0F)
                .lon(10.0F)
                .initiator(owner)
                .category(category)
                .requestModeration(true)
                .description("desc")
                .annotation("annotation")
                .publishedOn(LocalDateTime.now().plusDays(1))
                .participantLimit(10)
                .build());
    }

    @Test
    void testCommentToCommentRelation() {
        Comment comment = commentsRepository.save(Comment
                .builder()
                .text("abra")
                .eventId(1L)
                .author("vasya")
                .parentId(null)
                .pinned(false)
                .build());
        Comment subComment1 = commentsRepository.save(Comment
                .builder()
                .text("abra")
                .eventId(1L)
                .author("vasya")
                .parentId(comment.getId())
                .pinned(false)
                .build());
        Comment subComment2 = commentsRepository.save(Comment
                .builder()
                .text("abra")
                .eventId(1L)
                .author("vasya")
                .parentId(comment.getId())
                .pinned(false)
                .build());
        Comment subSubComment1 = commentsRepository.save(Comment
                .builder()
                .text("abra")
                .eventId(1L)
                .author("vasya")
                .parentId(subComment1.getId())
                .pinned(false)
                .build());
        em.clear();
        Event ev = eventRepository.findById(1L).orElseThrow();

        assertEquals(1, ev.getComments().size());
        Comment firstLevelComment = ev.getComments().stream().findFirst().get();
        List<Comment> secondLevelComments = firstLevelComment.getComments();
        assertEquals(2, secondLevelComments.size());
        Comment thirdLevelComment = secondLevelComments.stream().findFirst().get();

        assertEquals(1L, thirdLevelComment.getComments().size());
        assertEquals(0L, thirdLevelComment.getComments()
                .stream().findFirst().get()
                .getComments().size());
    }
}