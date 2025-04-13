package ru.mylink.mylink.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.mylink.mylink.model.entity.Session;

public interface SessionRepository extends JpaRepository<Session, String> {
    public Optional<Session> findByToken(String token);
}
