package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.error.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryDtoWithId create(CategoryDto dto) {
        Category category = Category.builder()
                .name(dto.getName())
                .build();
        Category savedCategory = categoryRepository.save(category);
        return CategoryDtoWithId.builder()
                .id(savedCategory.getId())
                .name(savedCategory.getName())
                .build();
    }

    public void delete(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    public CategoryDtoWithId edit(CategoryDtoWithId dto) {
        Category category = categoryRepository.findById(dto.getId()).orElseThrow(NotFoundException::new);
        category.setName(dto.getName());
        Category savedCategory = categoryRepository.save(category);
        return CategoryDtoMapper.fromCategory(savedCategory);
    }

    public List<CategoryDtoWithId> find(Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Category> categories = categoryRepository.findAll(page);
        return categories
                .stream()
                .map(CategoryDtoMapper::fromCategory)
                .collect(Collectors.toList());
    }

    public CategoryDtoWithId findById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(NotFoundException::new);
        return CategoryDtoMapper.fromCategory(category);
    }
}
