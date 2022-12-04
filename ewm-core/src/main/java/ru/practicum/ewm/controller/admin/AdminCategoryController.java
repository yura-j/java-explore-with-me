package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.CategoryDto;
import ru.practicum.ewm.category.CategoryDtoWithId;
import ru.practicum.ewm.category.CategoryService;
import ru.practicum.ewm.marker.Base;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
@Slf4j
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public CategoryDtoWithId createCategory(
            @RequestBody @Validated(Base.class) CategoryDto dto
    ) {
        log.info("Создана категория");

        return categoryService.create(dto);
    }

    @PatchMapping
    public CategoryDtoWithId editCategory(
            @RequestBody CategoryDtoWithId dto) {
        log.info("Отредактирована категория");

        return categoryService.edit(dto);
    }

    @DeleteMapping("/{categoryId}")
    public void deleteCategory(
            @PathVariable Long categoryId
    ) {
        log.info("Удалена категория {}", categoryId);
        categoryService.delete(categoryId);
    }
}
