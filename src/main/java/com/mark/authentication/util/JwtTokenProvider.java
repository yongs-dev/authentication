package com.mark.authentication.util;

import com.mark.authentication.app.user.domain.User;
import com.mark.authentication.app.user.dto.JwtTokenResponse;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class JwtTokenProvider {

    private final RedisTemplate<String, String> redisTemplate;

    private final String JWT_TOKEN_SECRET_KEY;
    private final int JWT_TOKEN_ACCESS_EXPIRATION_TIME;
    private final int JWT_TOKEN_REFRESH_EXPIRATION_TIME;

    public JwtTokenProvider(
            final RedisTemplate<String, String> redisTemplate,
            @Value("${jwt.token.secret-key}") final String JWT_TOKEN_SECRET_KEY,
            @Value("${jwt.token.access-expiration-time}") final int JWT_TOKEN_ACCESS_EXPIRATION_TIME,
            @Value("${jwt.token.refresh-expiration-time}") final int JWT_TOKEN_REFRESH_EXPIRATION_TIME
    ) {
        this.redisTemplate = redisTemplate;
        this.JWT_TOKEN_SECRET_KEY = JWT_TOKEN_SECRET_KEY;
        this.JWT_TOKEN_ACCESS_EXPIRATION_TIME = JWT_TOKEN_ACCESS_EXPIRATION_TIME;
        this.JWT_TOKEN_REFRESH_EXPIRATION_TIME = JWT_TOKEN_REFRESH_EXPIRATION_TIME;
    }

    public JwtTokenResponse generateJwtToken(final User user) {
        return JwtTokenResponse.builder()
                .email(user.getEmail())
                .accessToken(generateJwtAccessToken(user))
                .refreshToken(generateJwtRefreshToken(user))
                .build();
    }

    private String generateJwtAccessToken(final User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setHeader(createHeader())
                .setClaims(createClaims(user))
                .setExpiration(createExpireDate(JWT_TOKEN_ACCESS_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, createSigningKey())
                .compact();
    }

    private String generateJwtRefreshToken(final User user) {
        String savedToken = redisTemplate.opsForValue().get(user.getEmail() + "|" + user.getProvider());

        if (savedToken == null) {
            String newToken = Jwts.builder()
                    .setSubject(user.getEmail())
                    .setHeader(createHeader())
                    .setClaims(createClaims(user))
                    .setExpiration(createExpireDate(JWT_TOKEN_REFRESH_EXPIRATION_TIME))
                    .signWith(SignatureAlgorithm.HS256, createSigningKey())
                    .compact();

            redisTemplate.opsForValue().set(
                    user.getEmail() + "|" + user.getProvider(),
                    newToken,
                    JWT_TOKEN_REFRESH_EXPIRATION_TIME,
                    TimeUnit.SECONDS
            );

            return newToken;
        } else {
            return savedToken;
        }
    }

    public boolean isValidToken(final String token) {
        try {
            final Claims claims = getClaimsFormToken(token);
            log.info("expireTime: {}", claims.getExpiration());
            log.info("email: {}", claims.get("email"));
            log.info("role: {}", claims.get("role"));
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Token Expired");
        } catch (JwtException e) {
            log.error("Token Tampered");
        } catch (NullPointerException e) {
            log.error("Token is null");
        }

        return false;
    }

    private Map<String, Object> createHeader() {
        return new HashMap<>() {{
            put("typ", "JWT");
            put("alg", "HS256");
            put("regDate", System.currentTimeMillis());
        }};
    }

    private Map<String, Object> createClaims(final User user) {
        return new HashMap<>() {{
            put("email", user.getEmail());
            put("role", user.getRole());
            put("provider", user.getProvider());
        }};
    }

    private Date createExpireDate(int expirationTime) {
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, expirationTime);
        return c.getTime();
    }

    private Key createSigningKey() {
        final byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(JWT_TOKEN_SECRET_KEY);
        return new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    private Claims getClaimsFormToken(final String token) {
        return Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(JWT_TOKEN_SECRET_KEY))
                .parseClaimsJws(token)
                .getBody();
    }

    public String getEmailFromToken(final String token) {
        final Claims claims = getClaimsFormToken(token);
        return (String) claims.get("email");
    }

    public String getProviderFromToken(final String token) {
        final Claims claims = getClaimsFormToken(token);
        return (String) claims.get("provider");
    }

    public String getTokenFromHeader(final String header) {
        return header.split(" ")[1];
    }
}
