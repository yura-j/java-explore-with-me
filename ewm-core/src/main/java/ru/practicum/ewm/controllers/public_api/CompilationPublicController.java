package ru.practicum.ewm.controllers.public_api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.CompilationOutputDto;
import ru.practicum.ewm.compilation.CompilationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
@Slf4j
public class CompilationPublicController {

    private final CompilationService compilationService;

    @GetMapping
    List<CompilationOutputDto> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        log.info("Запрошены подборки");
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compilationId}")
    public CompilationOutputDto get (
            @PathVariable Long compilationId
    ) {
        log.info("Запрошена подборка, id {}", compilationId);
        return compilationService.get(compilationId);
    }
}
