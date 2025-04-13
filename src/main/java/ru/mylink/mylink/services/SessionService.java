package ru.mylink.mylink.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ru.mylink.mylink.model.entity.Session;
import ru.mylink.mylink.model.entity.User;
import ru.mylink.mylink.repositories.SessionRepository;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final CookieService cookieService;

    /** Создает новый объект сессии (detached), гарантируя, что сессии с таким токеном не существует*/
    private Session generateEmptySession(){
        var session = new Session();
        do
            session.setToken(UUID.randomUUID().toString());
        while (findByToken(session.getToken()).isPresent());

        return session;
    }

    public Optional<Session> findByToken(String token) {
        return sessionRepository.findByToken(token);
    }

    public Session createAnonymous() {
        var session = generateEmptySession();
        return sessionRepository.save(session);
    }

    public Session createForUser(User user) {
        var session = generateEmptySession();
        session.setUser(user);
        return sessionRepository.save(session);
    }
    
    /** Возвращает сессию пользователя. Если отсутствует, то анонимную сессию. Если отсутствует, то Optional.empty */
    public Optional<Session> extractFromRequest(HttpServletRequest request){
        var userToken = cookieService.getUserToken(request);
        if (userToken.isPresent())
            return findByToken(userToken.get());

        var anonymousToken = cookieService.getAnonymousToken(request);
        if (anonymousToken.isPresent())
            return findByToken(anonymousToken.get());

        return Optional.empty();
    }
}
