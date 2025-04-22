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
import lombok.extern.log4j.Log4j2;
import ru.mylink.mylink.services.CookieService;

@Log4j2
@Component
@RequiredArgsConstructor
public class SessionUpdateFilter implements Filter {

    private final CookieService cookieService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        var httpRequest = (HttpServletRequest) request;
        var httpResponse = (HttpServletResponse) response;
        
        // Проверяем, есть ли активная пользовательская сессия в cookies
        var optionalCookie = cookieService.getUserTokenCookie(httpRequest);

        // Обновляем оставшееся время для существующей пользовательской сессии
        if (optionalCookie.isPresent())
            cookieService.addUserTokenCookie(httpResponse, optionalCookie.get().getValue());
        
        chain.doFilter(request, response);
    }
    
}
