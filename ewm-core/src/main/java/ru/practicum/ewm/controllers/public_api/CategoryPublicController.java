package ru.practicum.ewm.controllers.public_api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.CategoryDtoWithId;
import ru.practicum.ewm.category.CategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@Slf4j
public class CategoryPublicController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDtoWithId> getCategories(
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        return categoryService.find(from, size);
    }

    @GetMapping("/{categoryId}")
    public CategoryDtoWithId get(
           @PathVariable Long categoryId
    ) {
        return categoryService.findById(categoryId);
    }
}
