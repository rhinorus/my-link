package ru.mylink.mylink.repositories;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.mylink.mylink.model.entity.Link;

public interface LinkRepository extends JpaRepository<Link, String>{
    
    public Optional<Link> findFirstByShortUrl(String shortUrl);
    public Set<Link> findAllBySessionToken(String sessionToken);
    public Set<Link> findAllByUserTelegramId(Long userTelegramId);

    @Query(nativeQuery = true, value = "SELECT SUM(count) FROM links")
    public Long totalClicks();

}
