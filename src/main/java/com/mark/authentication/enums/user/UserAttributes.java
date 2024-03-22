package com.mark.authentication.enums.user;

import com.mark.authentication.app.user.dto.UserProfile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public enum UserAttributes {
    NAVER("NAVER", (attribute) -> {
        final Map<String, String> response = (Map) attribute.get("response");

        return UserProfile.builder()
                .email(response.get("email"))
                .name(response.get("name"))
                .provider("NAVER")
                .build();
    }),
    KAKAO("KAKAO", (attribute) -> {
        final Map<String, Object> account = (Map) attribute.get("kakao_account");
        final Map<String, String> profile = (Map) account.get("profile");

        return UserProfile.builder()
                .email((String) account.get("email"))
                .name(profile.get("nickname"))
                .provider("KAKAO")
                .build();
    })
    ;

    private final String registrationId;
    private final Function<Map<String, Object>, UserProfile> of;

    public static UserProfile extract(final String registrationId, final Map<String, Object> attributes) {
        return Arrays.stream(values())
                .filter(v -> registrationId.equals(v.registrationId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .of.apply(attributes);
    }
}
