package com.mark.authentication.app.user.dto;

import com.mark.authentication.app.user.domain.User;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class UserListResponse {

    private final List<User> userList;
}
