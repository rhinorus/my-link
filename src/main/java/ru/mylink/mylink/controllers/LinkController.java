package ru.mylink.mylink.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import ru.mylink.mylink.model.entity.Link;
import ru.mylink.mylink.services.LinkService;

@Log4j2
@RestController
@RequestMapping(value = "/links")
public class LinkController {

    LinkService linkService;

    public LinkController(LinkService linkService){
        this.linkService = linkService;
    }

    @GetMapping
    public Iterable<Link> list(){
        return linkService.findAll();
    }
    
    @GetMapping(value = "by-short-url/{shortUrl}")
    public ResponseEntity<Link> get(@PathVariable String shortUrl){
        var optionalLink = linkService.find(shortUrl);
        return ResponseEntity.ofNullable(optionalLink.orElseGet(null));
    }

    @PutMapping
    public ResponseEntity<Link> put(@RequestBody Link link) {
        return ResponseEntity.ok(linkService.put(link));
    }

    @DeleteMapping(value = "{shortUrl}")
    public ResponseEntity<String> delete(@PathVariable String shortUrl){
        if(linkService.delete(shortUrl))
            return ResponseEntity.ok().build();

        return ResponseEntity.notFound().build();
    }

}
