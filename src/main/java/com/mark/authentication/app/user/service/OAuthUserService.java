package com.mark.authentication.app.user.service;

import com.mark.authentication.app.user.domain.User;
import com.mark.authentication.app.user.dto.UserProfile;
import com.mark.authentication.app.user.repository.UserRepository;
import com.mark.authentication.enums.role.UserRole;
import com.mark.authentication.enums.user.UserAttributes;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OAuthUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        final OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        final String registrationId = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        final String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        final Map<String, Object> attributes = oAuth2User.getAttributes();
        final UserProfile userProfile = UserAttributes.extract(registrationId, attributes);
        final Map<String, Object> customAttributes = getCustomAttribute(registrationId, userNameAttributeName, attributes, userProfile);

        final User user = getOrSaveUser(userProfile);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_".concat(user.getRole().getValue()))),
                customAttributes,
                userNameAttributeName
        );
    }

    private Map<String, Object> getCustomAttribute(final String registrationId, final String userNameAttributeName, final Map<String, Object> attributes, final UserProfile userProfile) {
        final Map<String, Object> customAttribute = new ConcurrentHashMap<>();
        customAttribute.put(userNameAttributeName, attributes.get(userNameAttributeName));
        customAttribute.put("provider", registrationId);
        customAttribute.put("name", userProfile.getName());
        customAttribute.put("email", userProfile.getEmail());
        return customAttribute;
    }

    private User getOrSaveUser(final UserProfile userProfile) {
        return userRepository.findByEmailAndProvider(userProfile.getEmail(), userProfile.getProvider())
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(userProfile.getEmail())
                                .name(userProfile.getName())
                                .role(UserRole.ROLE_USER)
                                .provider(userProfile.getProvider())
                                .build())
                );
    }
}
