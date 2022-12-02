package ru.practicum.ewm.controllers.admin;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.markers.Base;
import ru.practicum.ewm.user.UserDto;
import ru.practicum.ewm.user.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class AdminUsersController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        log.debug("Запрошены пользователи");

        return userService.getUsers(ids, from, size);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.info("Запрошен пользователь {}", userId);

        return userService.getUser(userId);
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Validated(Base.class) UserDto dto) {
        log.info("Создан пользователь");

        return userService.createUser(dto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Удален пользователь {}", userId);
        userService.deleteUser(userId);
    }
}
