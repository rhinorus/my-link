package ru.mylink.mylink.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.mylink.mylink.model.entity.Link;

public interface LinkRepository extends JpaRepository<Link, String>{
    
    public Optional<Link> findFirstByShortUrl(String shortUrl);
    public Iterable<Link> findAllBySessionToken(String sessionToken);
    public Iterable<Link> findAllByUserTelegramId(Long userTelegramId);

    @Query(nativeQuery = true, value = "SELECT SUM(count) FROM links")
    public Long totalClicks();

}
