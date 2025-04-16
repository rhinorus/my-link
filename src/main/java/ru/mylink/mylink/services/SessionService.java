package ru.mylink.mylink.services;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ru.mylink.mylink.model.entity.Session;
import ru.mylink.mylink.model.entity.SessionCentralizedAuthRequest;
import ru.mylink.mylink.model.entity.User;
import ru.mylink.mylink.repositories.SessionCentralizedAuthRequestRepository;
import ru.mylink.mylink.repositories.SessionRepository;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final SessionCentralizedAuthRequestRepository authRequestRepository;
    private final CookieService cookieService;
    private final UserService userService;

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
        // Сессия может быть только одна. Удаляем текущую, если есть
        var currentSession = user.getSession();
        if (Objects.nonNull(currentSession))
            sessionRepository.delete(currentSession);

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

    private void removeAuthRequestIfPresent(Session session) {
        if (Objects.nonNull(session.getRequest())){
            var existingRequest = session.getRequest();
            authRequestRepository.delete(existingRequest);
        }
    }

    /** Запоминает, что для пользователя с указанной сессией ожидается авторизация через запрос с указанным uuid */
    @Nullable
    public SessionCentralizedAuthRequest putRequest(String requestUuid, String sessionToken){
        var optionalSession = sessionRepository.findByToken(sessionToken);

        if (optionalSession.isEmpty())
            return null;
        
        var session = optionalSession.get();
        removeAuthRequestIfPresent(session);

        var request = new SessionCentralizedAuthRequest(requestUuid, session);
        return authRequestRepository.save(request);

    }

    public Optional<SessionCentralizedAuthRequest> findRequest(String requestUuid){
        return authRequestRepository.findByUuid(requestUuid); 
    }

    public void setUserForAuthRequest(String requestUuid, Long userTelegramId){
        var optionalRequest = findRequest(requestUuid);

        if (optionalRequest.isPresent()){
            var request = optionalRequest.get();
            var user = userService.getOrCreate(userTelegramId);
            
            request.setUser(user);
            authRequestRepository.save(request); 
        }
        
    }

}
