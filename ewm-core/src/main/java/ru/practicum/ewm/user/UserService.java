package ru.practicum.ewm.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.error.AlreadyExistException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userStorage;

    public List<UserDto> getUsers() {
        return userStorage
                .findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUser(Long userId) {
        User user = userStorage.findById(userId).orElseThrow();
        return UserMapper.toUserDto(user);
    }

    @Transactional
    public UserDto createUser(UserDto dto) {
        User user = UserMapper.toUser(dto);
        try {
            User savedUser = userStorage.save(user);
            return UserMapper.toUserDto(savedUser);
        } catch (Exception exception) {
            throw new AlreadyExistException("Такой email уже есть");
        }
    }

    @Transactional
    public void deleteUser(Long userId) {
        userStorage.deleteById(userId);
    }
}
