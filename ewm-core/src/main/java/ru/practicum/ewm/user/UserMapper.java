package ru.practicum.ewm.user;

public class UserMapper {
    public static UserDto toUserDto(User savedUser) {
        return UserDto.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .build();
    }

    public static User toUser(UserDto dto) {
        return User.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .name(dto.getName())
                .build();
    }
}
