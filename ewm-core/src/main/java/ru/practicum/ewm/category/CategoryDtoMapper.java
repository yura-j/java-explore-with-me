package ru.practicum.ewm.category;

public class CategoryDtoMapper {
    public static CategoryDtoWithId fromCategory(Category category) {
        return CategoryDtoWithId
                .builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
