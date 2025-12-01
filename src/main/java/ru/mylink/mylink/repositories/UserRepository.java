package ru.mylink.mylink.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mylink.mylink.model.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByTelegramId(Long telegramId);
}
