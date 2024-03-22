package com.mark.authentication.app.user.controller;

import com.mark.authentication.app.user.domain.AppUserInfos;
import com.mark.authentication.app.user.dto.JwtTokenResponse;
import com.mark.authentication.app.user.dto.SignUpRequest;
import com.mark.authentication.app.user.dto.UserListResponse;
import com.mark.authentication.app.user.dto.UserProfile;
import com.mark.authentication.app.user.service.UserService;
import com.mark.authentication.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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


    @PostMapping("/reissue")
    public ResponseEntity<JwtTokenResponse> reissue(@RequestBody final UserProfile userProfile) {
        return ResponseEntity.ok(jwtTokenProvider.generateJwtToken(userService.findByEmailAndProvider(userProfile.getEmail(), userProfile.getProvider())));
    }

    @PostMapping("/loginInfo")
    public String oAuthLoginInfo() {
        final AppUserInfos oAuth2User = (AppUserInfos) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return oAuth2User.getAttributes().toString();
    }
}
