package ru.mylink.mylink.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.mylink.mylink.model.entity.Link;

import java.util.Optional;
import java.util.Set;

public interface LinkRepository extends JpaRepository<Link, String> {

    Optional<Link> findFirstByShortUrl(String shortUrl);

    Set<Link> findAllByAnonymousSessionToken(String sessionToken);

    Set<Link> findAllByUserTelegramId(Long userTelegramId);

    @Query(nativeQuery = true, value = "SELECT SUM(count) FROM links")
    Long totalClicks();
}
