package ru.mylink.mylink.controllers.api;

import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.mylink.mylink.model.dto.CentralizedAuthUser;
import ru.mylink.mylink.services.CookieService;
import ru.mylink.mylink.services.SessionService;
import ru.mylink.mylink.services.TelegramAuthService;


@Log4j2
@RestController
@RequestMapping(value = "/api/centralized-auth")
@RequiredArgsConstructor
public class CentralizedAuthController {

    private final SessionService sessionService;
    private final TelegramAuthService telegramAuthService;
    private final CookieService cookieService;

    @PostMapping
    public ResponseEntity<String> confirmAuth(@RequestBody CentralizedAuthUser userInfo) {
        log.info("Пользователь с id {} авторизовался по uuid {}", userInfo.getId(), userInfo.getUuid());

        // Отмечаем, что появился пользователь с заданным ID
        sessionService.setUserForAuthRequest(userInfo.getUuid(), userInfo.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("auth")
    public ResponseEntity<String> auth(HttpServletRequest request, HttpServletResponse response) {
        // Если авторизация для пользователя пришла, то 
        // в текущем запросе на авторизацию в сессии будет указан пользователь

        var optionalSession = sessionService.extractFromRequest(request);

        // Может не быть либо такой сессии ...
        if (optionalSession.isEmpty())
            return ResponseEntity.notFound().build();

        var session = optionalSession.get();
        var authRequest = session.getRequest();
        
        // ... либо отсутствовать запрос на авторизацию ...
        if (Objects.isNull(authRequest))
            return ResponseEntity.notFound().build();

        var user = authRequest.getUser();

        // ... либо по запросу не получен пользователь
        if (Objects.isNull(user))
            return ResponseEntity.notFound().build();

        // Успешная авторизация. Отдаем пользовательскую сессию
        var userSession = sessionService.createForUser(user);
        cookieService.addUserTokenCookie(response, userSession.getToken());

        // TODO: Привязать ссылки из текущей анонимной сессии пользователю?
        // TODO: Почистить запрос на авторизацию, как использованный

        return ResponseEntity.ok().build();
    }
    

    @GetMapping("url")
    public String requestAuthUrl(HttpServletRequest request) throws JsonProcessingException {
        var session = sessionService.extractFromRequest(request);
        return telegramAuthService.requestAuthUrl(session.get());
    }
    
}
