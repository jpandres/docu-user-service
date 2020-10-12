package com.jpandres.docuuserservice.service;

import com.jpandres.docuuserservice.data.UserVO;
import com.jpandres.docuuserservice.exception.DuplicatedUserException;
import com.jpandres.docuuserservice.mapper.UserMapper;
import com.jpandres.docuuserservice.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final Map<String, User> userRepo;
    private final UserMapper userMapper;

    public User createUser(UserVO user) {
        if (userRepo.values().stream().anyMatch(u -> u.getUsername().equals(user.getUsername()))) {
            throw new DuplicatedUserException("Username already exists " + user.getUsername());
        }
        User createdUser = User.builder()
                .id(UUID.randomUUID())
                .username(user.getUsername())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .age(user.getAge())
                .build();
        userRepo.put(createdUser.getId().toString(), createdUser);
        return createdUser;
    }

    public UserVO getUser(String userId) {
        return userMapper.modelToDto(userRepo.get(userId));
    }

    public List<UserVO> getUsers() {
        return userRepo.values().stream().map(userMapper::modelToDto).collect(Collectors.toList());
    }
}
