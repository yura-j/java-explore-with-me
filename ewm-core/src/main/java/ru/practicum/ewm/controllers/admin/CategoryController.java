package ru.practicum.ewm.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.CategoryDto;
import ru.practicum.ewm.category.CategorySaveDto;
import ru.practicum.ewm.category.CategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public CategorySaveDto createCategory(
            @RequestBody CategoryDto dto
            ) {
        return categoryService.create(dto);
    }

    @PatchMapping("/{categoryId}")
    public CategorySaveDto editCategory(
            @RequestBody CategoryDto dto,
            @PathVariable Long categoryId) {
        return categoryService.edit(categoryId, dto);
    }

    @DeleteMapping("/{categoryId}")
    public void deleteCategory(
            @PathVariable Long categoryId
    ) {
        categoryService.delete(categoryId);
    }
}
