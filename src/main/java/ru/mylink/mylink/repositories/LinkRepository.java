package ru.mylink.mylink.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.mylink.mylink.model.entity.Link;

public interface LinkRepository extends JpaRepository<Link, String>{
    
    public Optional<Link> findFirstByShortUrl(String shortUrl);

}
