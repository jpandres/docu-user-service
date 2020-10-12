package com.jpandres.docuuserservice.service;

import com.jpandres.docuuserservice.data.UserVO;
import com.jpandres.docuuserservice.exception.DuplicatedUserException;
import com.jpandres.docuuserservice.mapper.UserMapper;
import com.jpandres.docuuserservice.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private Map<String, User> userRepo;

    @Test
    void shouldCreateUserAndAddtoMap() {
        UserVO user = UserVO.builder()
                .username("pepe@pepe.com")
                .firstname("pepe")
                .lastname("smith")
                .age(22)
                .build();
        User createdUser = userService.createUser(user);

        assertThat(createdUser.getId()).isNotNull();
        verify(userRepo).put(anyString(), any(User.class));
    }

    @Test
    void shouldThrowDuplicated() {
        UserVO user = UserVO.builder()
                .username("pepe@pepe.com")
                .firstname("pepe")
                .lastname("smith")
                .age(22)
                .build();

        when(userRepo.values()).thenReturn(List.of(User.builder().username("pepe@pepe.com").build()));

        assertThatThrownBy(() -> {
            userService.createUser(user);

        }).isInstanceOf(DuplicatedUserException.class)
                .hasMessage("Username already exists pepe@pepe.com");
    }

}