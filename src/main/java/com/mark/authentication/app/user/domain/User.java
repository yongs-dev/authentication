package com.mark.authentication.app.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mark.authentication.app.common.domain.BaseEntity;
import com.mark.authentication.enums.role.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@Table(name = "MEMBER")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String email;

    @Column(name = "user_name")
    @Size(min = 2, message = "Name should have atleast 2 characters")
    private String name;

    @JsonIgnore
    @Column
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column
    @Builder.Default
    private String provider = "APP";
}
