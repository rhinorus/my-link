package ru.mylink.mylink.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.mylink.mylink.model.entity.AnonymousSession;

public interface SessionRepository extends JpaRepository<AnonymousSession, String> {
    public Optional<AnonymousSession> findByToken(String token);
}
