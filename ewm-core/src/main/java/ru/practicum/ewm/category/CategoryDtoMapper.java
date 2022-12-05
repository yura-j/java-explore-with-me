package ru.practicum.ewm.category;

public class CategoryDtoMapper {

    public static Category fromDto(CategoryDto dto) {
        return Category
                .builder()
                .name(dto.getName())
                .build();
    }

    public static CategoryDtoWithId toDto(Category category) {
        return CategoryDtoWithId
                .builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
