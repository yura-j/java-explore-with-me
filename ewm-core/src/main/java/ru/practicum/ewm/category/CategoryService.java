package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.error.NotFoundException;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategorySaveDto create(CategoryDto dto) {
        Category category = Category.builder()
                .name(dto.getName())
                .build();
        Category savedCategory = categoryRepository.save(category);
        return CategorySaveDto.builder()
                .id(savedCategory.getId())
                .name(savedCategory.getName())
                .build();
    }

    public void delete(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    public CategorySaveDto edit(Long categoryId, CategoryDto dto) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(NotFoundException::new);
        category.setName(dto.getName());
        Category savedCategory = categoryRepository.save(category);
        return CategorySaveDto.builder()
                .id(savedCategory.getId())
                .name(savedCategory.getName())
                .build();
    }
}
