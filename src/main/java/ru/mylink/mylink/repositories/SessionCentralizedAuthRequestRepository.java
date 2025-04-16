package ru.mylink.mylink.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.mylink.mylink.model.entity.SessionCentralizedAuthRequest;

public interface SessionCentralizedAuthRequestRepository extends JpaRepository<SessionCentralizedAuthRequest, String> {
    public Optional<SessionCentralizedAuthRequest> findByUuid(String uuid);
}
