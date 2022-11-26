package ru.practicum.ewm.controllers.admin;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.UserDto;
import ru.practicum.ewm.user.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers() {
        log.debug("Запрошены пользователи");
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.debug("Запрошен пользователь {}", userId);
        return userService.getUser(userId);
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto dto) {
        log.debug("Создан пользователь");
        return userService.createUser(dto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.debug("Удален пользователь {}", userId);
        System.out.println(userService.getUsers());
        userService.deleteUser(userId);
        System.out.println(userService.getUsers());
    }
}
