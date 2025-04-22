package ru.mylink.mylink.services;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.mylink.mylink.model.entity.Link;
import ru.mylink.mylink.model.entity.Session;
import ru.mylink.mylink.repositories.LinkRepository;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkRepository linkRepository; 
    private final UserService userService;
    
    public Optional<Link> find(String shortUrl) {
        return linkRepository.findFirstByShortUrl(shortUrl.toLowerCase());
    }

    public Iterable<Link> findAll(){
        return linkRepository.findAll();
    }

    public Iterable<Link> findAllBySession(Session session){
        return linkRepository.findAllBySessionToken(session.getToken());
    }

    public Iterable<Link> findAllByUserTelegramId(Long userTelegramId){
        return linkRepository.findAllByUserTelegramId(userTelegramId);
    }

    public Link put(Link link){
        return linkRepository.save(link);
    }

    public Boolean deleteIfExists(String shortUrl){
        var link = find(shortUrl);

        if (link.isEmpty())
            return false;

        linkRepository.delete(link.get());
        return true;
    }

    public Boolean isAuthorized(Link link, Session session){

        // Либо ссылка создана в рамках текущей сессии
        if (Objects.nonNull(link.getSession()))
            if (link.getSession().getToken().equals(session.getToken()))
                return true;

        // Либо ссылка принадлежит текущему пользователю
        if (Objects.nonNull(session.getUser()))
            if (Objects.nonNull(link.getUser()))
                return link.getUser().getTelegramId().equals(session.getUser().getTelegramId());

        return false;
    }

    public void transferLinksToUser(String anonymousSessionToken, Long userTelegramId) {
        var user = userService.getOrCreate(userTelegramId);
        var links = linkRepository.findAllBySessionToken(anonymousSessionToken);

        links.forEach(link -> link.setUser(user));
        linkRepository.saveAll(links);
    }

    public Long count() {
        return linkRepository.count();
    }

    public Long totalClicks(){
        return linkRepository.totalClicks();
    }

}
