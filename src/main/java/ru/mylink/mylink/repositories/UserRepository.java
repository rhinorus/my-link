package ru.mylink.mylink.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.mylink.mylink.model.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
}
