package com.jpandres.docuuserservice.controller;

import com.jpandres.docuuserservice.data.UserVO;
import com.jpandres.docuuserservice.service.UserService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Timed(value = "user.creation.timer", description = "Timer for the user creation endpoint")
    @Counted(value = "user.creation.counter", description = "User creation counter")
    @PostMapping()
    public ResponseEntity<Void> createUser(@Validated  @RequestBody UserVO user) {
        log.debug("User to be created: {}", user);
        var newUser = userService.createUser(user);
        return ResponseEntity.created(URI.create("/api/v1/users/" + newUser.getId())).build();
    }

    @GetMapping("/{userId}")
    @Counted(value = "user.get.counter", description = "User get counter")
    public ResponseEntity<UserVO> getUser(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @GetMapping()
    public ResponseEntity<List<UserVO>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

}
