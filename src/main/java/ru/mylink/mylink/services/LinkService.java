package ru.mylink.mylink.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mylink.mylink.model.entity.AnonymousSession;
import ru.mylink.mylink.model.entity.Link;
import ru.mylink.mylink.model.entity.User;
import ru.mylink.mylink.repositories.LinkRepository;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkRepository linkRepository;

    public Optional<Link> find(String shortUrl) {
        return linkRepository.findFirstByShortUrl(shortUrl.toLowerCase());
    }

    public Set<Link> findAllBySession(AnonymousSession anonymousSession) {
        return linkRepository.findAllByAnonymousSessionToken(anonymousSession.getToken());
    }

    public Set<Link> findAllByUserTelegramId(Long userTelegramId) {
        return linkRepository.findAllByUserTelegramId(userTelegramId);
    }

    public Link put(Link link) {
        return linkRepository.save(link);
    }

    public Boolean deleteIfExists(String shortUrl) {
        var link = find(shortUrl);

        if (link.isEmpty())
            return false;

        linkRepository.delete(link.get());
        return true;
    }

    public Boolean isAuthorized(Link link, Optional<AnonymousSession> anonymousSession, Optional<User> user) {
        // Либо ссылка создана в рамках текущей сессии
        if (link.getAnonymousSession() != null && anonymousSession.isPresent()) {
            if (link.getAnonymousSession().getToken().equals(anonymousSession.get().getToken())) {
                return true;
            }
        }
        // Либо ссылка принадлежит текущему пользователю
        if (link.getUser() != null && user.isPresent()) {
            return link.getUser().getTelegramId().equals(user.get().getTelegramId());
        }

        return false;
    }

    public Long count() {
        return linkRepository.count();
    }

    public Long totalClicks() {
        return linkRepository.totalClicks();
    }
}
