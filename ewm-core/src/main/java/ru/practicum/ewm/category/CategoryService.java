package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryDtoWithId create(CategoryDto dto) {
        Category category = CategoryDtoMapper.fromDto(dto);

        return CategoryDtoMapper.toDto(categoryRepository.save(category));
    }

    public void delete(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    public CategoryDtoWithId edit(CategoryDtoWithId dto) {
        Category category = categoryRepository
                .findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Категория не найдена"));
        category.setName(dto.getName());

        return CategoryDtoMapper.toDto(categoryRepository.save(category));
    }

    public List<CategoryDtoWithId> get(Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Category> categories = categoryRepository.findAll(page);

        return categories
                .stream()
                .map(CategoryDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    public CategoryDtoWithId getById(Long categoryId) {
        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Категория не найдена"));

        return CategoryDtoMapper.toDto(category);
    }
}
