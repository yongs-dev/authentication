package com.mark.authentication.app.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mark.authentication.app.user.domain.User;
import com.mark.authentication.app.user.dto.JwtTokenResponse;
import com.mark.authentication.app.user.dto.SignUpRequest;
import com.mark.authentication.app.user.dto.UserListResponse;
import com.mark.authentication.app.user.service.UserServiceImpl;
import com.mark.authentication.enums.role.UserRole;
import com.mark.authentication.util.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private static final String EMAIL = "yongseok993@gmail.com";
    private static final String PASSWORD = "Dydtjr@12";

    @InjectMocks
    private UserController userController;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }
    
    @Test
    public void signUpSuccess() throws Exception {
        // given
        final SignUpRequest signUpRequest = signUpRequest();
        final User user = User.builder().email("A").password("B").role(UserRole.ROLE_USER).build();
        final JwtTokenResponse jwtTokenResponse = jwtTokenResponse();

        doReturn(false).when(userService).isEmailDuplicated(signUpRequest.getEmail());
        doReturn(user).when(userService).signUp(any(SignUpRequest.class));
        doReturn(jwtTokenResponse).when(jwtTokenProvider).generateJwtToken(user);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/users/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsBytes(signUpRequest))
        );

        // then
        final MvcResult mvcResult = resultActions.andExpect(status().isOk()).andReturn();
        final String token = mvcResult.getResponse().getContentAsString();
        assertThat(token).isNotNull();
    }

    @Test
    public void getUserList() throws Exception {
        // given
        doReturn(userList()).when(userService).findAll();

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/users/list")
        );

        // then
        final MvcResult mvcResult = resultActions.andExpect(status().isOk()).andReturn();
        final UserListResponse response = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), UserListResponse.class);
        assertThat(response.getUserList().size()).isEqualTo(5);
    }

    private SignUpRequest signUpRequest() {
        return SignUpRequest.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();
    }

    private JwtTokenResponse jwtTokenResponse() {
        return JwtTokenResponse.builder()
                .email("A")
                .accessToken("at")
                .refreshToken("rt")
                .build();
    }

    private List<User> userList() {
        final List<User> userList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            userList.add(new User("test@test.com", "", "test", UserRole.ROLE_USER));
        }
        return userList;
    }
}
