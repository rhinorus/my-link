package ru.mylink.mylink.controllers.api;

import java.util.HashSet;
import java.util.Objects;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.mylink.mylink.model.entity.Link;
import ru.mylink.mylink.services.LinkService;
import ru.mylink.mylink.services.SessionService;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/links")
public class LinkController {

    private final Integer MAX_NUMBER_OF_LINKS = 99;

    private final LinkService linkService;
    private final SessionService sessionService;

    @GetMapping
    public Iterable<Link> list(HttpServletRequest request){
        var session = sessionService.extractFromRequest(request);
        var links = new HashSet<Link>();

        if (Objects.nonNull(session.get().getUser())) {
            var userLinks = linkService.findAllByUserTelegramId(
                session.get().getUser().getTelegramId()
            );
            userLinks.forEach(link -> links.add(link));
        }

        var anonymousLinks = linkService.findAllBySession(session.get());
        anonymousLinks.forEach(link -> links.add(link));

        return links;
    }
    
    @GetMapping(value = "by-short-url/{shortUrl}")
    public ResponseEntity<Link> get(@PathVariable String shortUrl){
        var optionalLink = linkService.find(shortUrl);
        return ResponseEntity.of(optionalLink);
    }

    @PutMapping
    public ResponseEntity<Link> put(@RequestBody Link link, HttpServletRequest request) {
        var session = sessionService.extractFromRequest(request);
        link.setUser(session.get().getUser());

        // Очистка последовательности короткой ссылки от недопустимых символов
        Pattern pattern = Pattern.compile("[^a-zA-Zа-яА-ЯёЁ0-9-]");
        link.setShortUrl(
            pattern.matcher(link.getShortUrl())
                .replaceAll("")
        );

        // Ссылка должна либо не существовать
        // Либо принадлежать текущему пользователю
        var optionalLink = linkService.find(link.getShortUrl());
        if (optionalLink.isPresent()){
            if (!linkService.isAuthorized(link, session.get()))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            // Если ссылка не существует, то число созданных ссылок пользователя должно быть меньше 100
            if (Objects.nonNull(session.get().getUser())){
                var user = session.get().getUser();
                var userLinks = linkService.findAllByUserTelegramId(user.getTelegramId());

                if (userLinks.size() >= MAX_NUMBER_OF_LINKS) 
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        // Если текущая сессия анонимная, то привязываем ссылку к ней, если не достигнут лимит
        if (Objects.isNull(session.get().getUser())){
            var sessionLinks = linkService.findAllBySession(session.get());

            if (sessionLinks.size() >= MAX_NUMBER_OF_LINKS)
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); 

            link.setSession(session.get());
        }

        return ResponseEntity.ok(linkService.put(link));
    }

    @DeleteMapping(value = "{shortUrl}")
    public ResponseEntity<String> delete(@PathVariable(name="shortUrl") Link link, HttpServletRequest request){
        var session = sessionService.extractFromRequest(request);

        if (session.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (linkService.isAuthorized(link, session.get())){
            if(linkService.deleteIfExists(link.getShortUrl())){
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        }
            
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

}
