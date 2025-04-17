package ru.mylink.mylink.controllers.api;

import java.util.HashSet;
import java.util.Objects;

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

        if (Objects.isNull(session.get().getUser()))
            link.setSession(session.get());

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
            
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
