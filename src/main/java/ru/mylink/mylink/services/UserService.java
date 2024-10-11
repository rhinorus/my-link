package ru.mylink.mylink.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;
import ru.mylink.mylink.model.entity.User;
import ru.mylink.mylink.repositories.UserRepository;

@Log4j2
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    
    public void save(User user) {
        userRepository.save(user);
    }

}
