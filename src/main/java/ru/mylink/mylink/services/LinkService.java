package ru.mylink.mylink.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.mylink.mylink.model.entity.Link;
import ru.mylink.mylink.repositories.LinkRepository;

@Service
public class LinkService {

    @Autowired
    LinkRepository linkRepository;
    
    public Optional<Link> find(String shortUrl) {
        return linkRepository.findFirstByShortUrl(shortUrl.toLowerCase());
    }

    public Iterable<Link> findAll(){
        return linkRepository.findAll();
    }

    public Link put(Link link){
        // TODO: Валидировать пользователя, который предпринял попытку сохранения
        return linkRepository.save(link);
    }

    public Boolean delete(String shortUrl){
        // TODO: Валидировать пользователя, который предпринял попытку удаления
        var link = find(shortUrl);

        if (link.isEmpty())
            return false;

        linkRepository.delete(link.get());
        return true;
    }

}
