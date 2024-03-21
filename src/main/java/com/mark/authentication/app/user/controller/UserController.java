package com.mark.authentication.app.user.controller;

import com.mark.authentication.app.user.dto.JwtTokenResponse;
import com.mark.authentication.app.user.dto.SignUpRequest;
import com.mark.authentication.app.user.dto.UserListResponse;
import com.mark.authentication.app.user.dto.UserRequest;
import com.mark.authentication.app.user.service.UserService;
import com.mark.authentication.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @PostMapping("/signUp")
    public ResponseEntity<JwtTokenResponse> signUp(@RequestBody final SignUpRequest signUpRequest) {
        return userService.isEmailDuplicated(signUpRequest.getEmail())
                ? ResponseEntity.badRequest().build()
                : ResponseEntity.ok(jwtTokenProvider.generateJwtToken(userService.signUp(signUpRequest)));
    }

    @GetMapping("/list")
    public ResponseEntity<UserListResponse> findAll() {
        final UserListResponse userListResponse = UserListResponse.builder()
                .userList(userService.findAll())
                .build();

        return ResponseEntity.ok(userListResponse);
    }

    @PostMapping("reissue")
    public ResponseEntity<JwtTokenResponse> reissue(@RequestBody final UserRequest userRequest) {
        return ResponseEntity.ok(jwtTokenProvider.generateJwtToken(userService.findByEmail(userRequest.getEmail())));
    }
}
