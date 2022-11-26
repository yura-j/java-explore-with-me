package ru.practicum.ewm.category;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategorySaveDto {
    private Long id;
    private String name;
}
