package ru.mylink.mylink.services;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.mylink.mylink.model.entity.User;
import ru.mylink.mylink.repositories.UserRepository;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    
    public void save(User user) {
        userRepository.save(user);
    }

    public User getOrCreate(Long telegramId) {
        var optionalUser = userRepository.findByTelegramId(telegramId);
        if (optionalUser.isPresent())
            return optionalUser.get();

        return userRepository.save(new User(telegramId));
    }

    public Long count(){
        return userRepository.count();
    }
}
