package ru.mylink.mylink.services;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CookieService {

    private final String ANONYMOUS_TOKEN_COOKIE_NAME    = "ANONYMOUS_TOKEN";
    private final String USER_TOKEN_COOKIE_NAME         = "USER_TOKEN";

    private Cookie buildAnonymousCookie(String token){
        var cookie = new Cookie(
            ANONYMOUS_TOKEN_COOKIE_NAME,
            token
        );
        
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 30); 

        return cookie;
    } 

    private Cookie buildUserCookie(String token) {
        var cookie = new Cookie(
            USER_TOKEN_COOKIE_NAME,
            token
        );

        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 30);
        
        return cookie;
    }

    public Optional<Cookie> find(HttpServletRequest request, String cookieName){
        var cookies = request.getCookies();

        if (Objects.isNull(cookies))
            return Optional.empty();

        return Arrays.stream(cookies)
            .filter(c -> c.getName().equals(cookieName))
            .findFirst();
    }

    public Optional<String> getAnonymousToken(HttpServletRequest request){
        var cookie = find(request, ANONYMOUS_TOKEN_COOKIE_NAME);

        if (cookie.isPresent())
            return Optional.of(cookie.get().getValue());

        return Optional.empty();
    }

    public Optional<String> getUserToken(HttpServletRequest request){
        var cookie = find(request, USER_TOKEN_COOKIE_NAME);

        if (cookie.isPresent())
            return Optional.of(cookie.get().getValue());
        
        return Optional.empty();
    }

    public Optional<Cookie> getUserTokenCookie(HttpServletRequest request) {
        return find(request, USER_TOKEN_COOKIE_NAME);
    }

    public void addAnonymousTokenCookie(HttpServletResponse response, String sessionToken){
        var anonymousCookie = buildAnonymousCookie(sessionToken);
        response.addCookie(anonymousCookie);
    }

    public void addUserTokenCookie(HttpServletResponse response, String sessionToken) {
        var userCookie = buildUserCookie(sessionToken);
        response.addCookie(userCookie);
    }

}
