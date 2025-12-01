package ru.mylink.mylink.controllers.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mylink.mylink.model.entity.Link;
import ru.mylink.mylink.services.LinkService;
import ru.mylink.mylink.services.SessionService;

import java.util.HashSet;
import java.util.regex.Pattern;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/links")
public class LinkController {

    private final static Integer MAX_NUMBER_OF_LINKS = 99;

    private final LinkService linkService;

    private final SessionService sessionService;

    @GetMapping
    public Iterable<Link> list(HttpServletRequest request) {
        var anonymousSession = sessionService.getAnonymousFromRequest(request);
        var optionalUser = sessionService.getUserFromRequest(request);

        var links = new HashSet<Link>();

        if (optionalUser.isPresent()) {
            var user = optionalUser.get();
            var userLinks = linkService.findAllByUserTelegramId(user.getTelegramId());
            links.addAll(userLinks);
        }

        if (anonymousSession.isPresent()) {
            var anonymousLinks = linkService.findAllBySession(anonymousSession.get());
            links.addAll(anonymousLinks);
        }

        return links;
    }

    @GetMapping(value = "by-short-url/{shortUrl}")
    public ResponseEntity<Link> get(@PathVariable String shortUrl) {
        var optionalLink = linkService.find(shortUrl);
        return ResponseEntity.of(optionalLink);
    }

    @PutMapping
    public ResponseEntity<Link> put(@RequestBody Link link, HttpServletRequest request) {
        var optionalUser = sessionService.getUserFromRequest(request);
        var anonymousSession = sessionService.getAnonymousFromRequest(request);

        // Очистка последовательности короткой ссылки от недопустимых символов
        Pattern pattern = Pattern.compile("[^a-zA-Zа-яА-ЯёЁ0-9-]");
        link.setShortUrl(
            pattern.matcher(link.getShortUrl())
                .replaceAll("")
        );

        // Ссылка должна либо не существовать
        // Либо принадлежать текущему пользователю
        var optionalLink = linkService.find(link.getShortUrl());
        if (optionalLink.isPresent()) {
            if (!linkService.isAuthorized(link, anonymousSession, optionalUser)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            // Если ссылка не существует, то число созданных ссылок пользователя должно быть меньше 100
            if (optionalUser.isPresent()) {
                var user = optionalUser.get();
                var userLinks = linkService.findAllByUserTelegramId(user.getTelegramId());

                if (userLinks.size() >= MAX_NUMBER_OF_LINKS)
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        // Если текущая сессия анонимная, то привязываем ссылку к ней, если не достигнут лимит
        if (optionalUser.isEmpty()) {
            var sessionLinks = linkService.findAllBySession(anonymousSession.get());

            if (sessionLinks.size() >= MAX_NUMBER_OF_LINKS)
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

            link.setAnonymousSession(anonymousSession.get());
        } else {
            link.setUser(optionalUser.get());
        }

        return ResponseEntity.ok(linkService.put(link));
    }

    @DeleteMapping(value = "{shortUrl}")
    public ResponseEntity<String> delete(@PathVariable(name = "shortUrl") Link link, HttpServletRequest request) {
        var user = sessionService.getUserFromRequest(request);
        var anonymousSession = sessionService.getAnonymousFromRequest(request);

        if (anonymousSession.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (linkService.isAuthorized(link, anonymousSession, user)) {
            if (linkService.deleteIfExists(link.getShortUrl())) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
