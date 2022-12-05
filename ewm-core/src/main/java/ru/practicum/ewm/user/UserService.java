package ru.practicum.ewm.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userStorage;

    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<User> users = (null != ids) ? userStorage.findAllByIdIn(ids, page) : userStorage.findAll(page);

        return users
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
        User savedUser = userStorage.save(user);

        return UserMapper.toUserDto(savedUser);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userStorage.deleteById(userId);
    }
}
