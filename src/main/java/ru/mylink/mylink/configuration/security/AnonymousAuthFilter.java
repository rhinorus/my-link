package ru.mylink.mylink.configuration.security;

import java.io.IOException;

import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ru.mylink.mylink.services.CookieService;
import ru.mylink.mylink.services.SessionService;

/** Выдает токен для анонимного доступа пользователю */
@Component
@RequiredArgsConstructor
public class AnonymousAuthFilter implements Filter {

    private final CookieService cookieService;
    private final SessionService sessionService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        var httpRequest = (HttpServletRequest) request;
        var httpResponse = (HttpServletResponse) response;

        var optionalAnonymousToken = cookieService.getAnonymousToken(httpRequest);
        var optionalSession = sessionService.findByToken(optionalAnonymousToken.orElse("empty"));

        if (optionalSession.isEmpty()){
            var session = sessionService.createAnonymous();
            cookieService.addAnonymousTokenCookie(httpResponse, session.getToken());
        }
            
        chain.doFilter(request, response);
    }
    
}