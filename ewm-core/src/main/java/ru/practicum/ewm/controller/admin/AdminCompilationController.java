package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.CompilationInputDto;
import ru.practicum.ewm.compilation.CompilationOutputDto;
import ru.practicum.ewm.compilation.CompilationService;
import ru.practicum.ewm.marker.Base;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
@Slf4j
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    public CompilationOutputDto create(
            @RequestBody @Validated(Base.class) CompilationInputDto dto
    ) {
        log.info("Добавлена подборка, события {}", dto.getEvents());

        return compilationService.create(dto);
    }

    @DeleteMapping("/{compilationId}")
    public void delete(
            @PathVariable Long compilationId
    ) {
        log.info("Удалена подборка, id {}", compilationId);
        compilationService.delete(compilationId);
    }

    @DeleteMapping("/{compilationId}/events/{eventId}")
    public void deleteEvent(
            @PathVariable Long compilationId,
            @PathVariable Long eventId
    ) {
        log.info("Удалено событие из подборки, id {}", eventId);
        compilationService.deleteEvent(compilationId, eventId);
    }

    @PatchMapping("/{compilationId}/events/{eventId}")
    public void addEvent(
            @PathVariable Long compilationId,
            @PathVariable Long eventId
    ) {
        log.info("Событие добавлено в подборку, id {}", eventId);
        compilationService.addEvent(compilationId, eventId);
    }

    @PatchMapping("/{compilationId}/pin")
    public void pin(
            @PathVariable Long compilationId
    ) {
        log.info("Закреплена подборка, id {}", compilationId);
        compilationService.pin(compilationId);
    }

    @DeleteMapping("/{compilationId}/pin")
    public void unpin(
            @PathVariable Long compilationId
    ) {
        log.info("Откреплена подборка, id {}", compilationId);
        compilationService.unpin(compilationId);
    }
}
