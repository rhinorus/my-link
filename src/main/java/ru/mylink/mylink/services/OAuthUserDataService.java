package ru.mylink.mylink.services;

import com.oauth0.lib.config.OAuthUserDataProcessor;
import com.oauth0.lib.dto.response.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mylink.mylink.model.entity.User;
import ru.mylink.mylink.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class OAuthUserDataService implements OAuthUserDataProcessor {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void save(UserDTO userDTO) {
        var optionalUser = userRepository.findByTelegramId(userDTO.getId());
        if (optionalUser.isEmpty()) {
            var user = new User(userDTO);
            userRepository.save(user);
        }
    }
}
