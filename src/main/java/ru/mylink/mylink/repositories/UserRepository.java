package ru.mylink.mylink.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.mylink.mylink.model.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findByTelegramId(Long telegramId);
}
