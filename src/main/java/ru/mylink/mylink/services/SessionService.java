package ru.mylink.mylink.services;

import java.util.Optional;
import java.util.UUID;

import com.oauth0.lib.service.OauthSessionService;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ru.mylink.mylink.model.entity.AnonymousSession;
import ru.mylink.mylink.model.entity.User;
import ru.mylink.mylink.repositories.SessionRepository;
import ru.mylink.mylink.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    private final CookieService cookieService;

    private final UserRepository userRepository;

    private final OauthSessionService oauthSessionService;

    /**
     * Создает новый объект сессии (detached), гарантируя, что сессии с таким токеном не существует
     */
    private AnonymousSession generateEmptySession() {
        var session = new AnonymousSession();
        do
            session.setToken(UUID.randomUUID().toString());
        while (findByToken(session.getToken()).isPresent());

        return session;
    }

    public Optional<AnonymousSession> findByToken(String token) {
        return sessionRepository.findByToken(token);
    }

    public AnonymousSession createAnonymous() {
        var session = generateEmptySession();
        return sessionRepository.save(session);
    }

    /**
     * Возвращает анонимную сессию пользователя. Если отсутствует, то Optional.empty
     */
    public Optional<AnonymousSession> getAnonymousFromRequest(HttpServletRequest request) {
        var anonymousToken = cookieService.getAnonymousToken(request);
        if (anonymousToken.isPresent())
            return findByToken(anonymousToken.get());

        return Optional.empty();
    }

    /**
     * Возвращает авторизованного пользователя. Если отсутствует, то Optional.empty
     */
    public Optional<User> getUserFromRequest(HttpServletRequest request) {
        var userToken = cookieService.getUserToken(request);
        if (userToken.isPresent()) {
            var optionalId = oauthSessionService.auth(userToken.get());
            if (optionalId.isPresent()) {
                return userRepository.findById(optionalId.get());
            }
        }

        return Optional.empty();
    }
}
