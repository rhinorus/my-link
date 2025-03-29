package ru.mylink.mylink.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import ru.mylink.mylink.model.entity.User;
import ru.mylink.mylink.services.telegram.TelegramService;


@Log4j2
@RestController
public class AuthController {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    TelegramService telegramService;
    
    @PostMapping("/auth")
    ResponseEntity<String> auth(
        @RequestParam("user")       String jsonUser,
        @RequestParam("hash")       String hash,
        HttpServletRequest request
    ) throws JsonProcessingException  {
        var dataCheck = telegramService.buildDataCheckString(request.getParameterMap());
        var user = mapper.readValue(jsonUser, User.class);

        if(telegramService.isValid(dataCheck, hash)){
            log.info("Валидация прошла успешно для пользователя: {}", user.toString());
            return ResponseEntity.ok("Аутентификация прошла успешно");
        } else {
            log.warn("Валидация провалена для пользователя: {}", user.toString());
            return ResponseEntity.badRequest().body("Аутентификация не удалась");
        }

    }
}
