package ru.mylink.mylink.configuration.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.mylink.mylink.services.SessionService;

/** Выдает токен для анонимного доступа пользователю */
@Component
public class AnonymousAuthFilter implements Filter {

    private final String ANONYMOUS_COOKIE_TOKEN_NAME = "ANONYMOUS_TOKEN";
    private final SessionService sessionService;

    public AnonymousAuthFilter(SessionService sessionService){
        this.sessionService = sessionService;
    }

    /** Создает куки с анонимным токеном для пользователя */
    private Cookie buildAnonymousCookie(){
        var anonymousSession = sessionService.createAnonymous();

        var cookie = new Cookie(
            ANONYMOUS_COOKIE_TOKEN_NAME,
            anonymousSession.getToken()
        );
        
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 30); 

        return cookie;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        var httpRequest = (HttpServletRequest) request;
        var httpResponse = (HttpServletResponse) response;


        if (Objects.isNull(httpRequest.getCookies())){
            httpResponse.addCookie(buildAnonymousCookie());
        } else {
            // Если куки не пусты, пытаемся найти среди них токен
            var hasAnonymousToken = Arrays.stream(httpRequest.getCookies())
                .anyMatch(c -> c.getName().equals(ANONYMOUS_COOKIE_TOKEN_NAME));

            if (!hasAnonymousToken)
                httpResponse.addCookie(buildAnonymousCookie());
        }
            
        chain.doFilter(request, response);
    }
    
}