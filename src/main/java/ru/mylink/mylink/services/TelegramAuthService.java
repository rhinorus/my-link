package ru.mylink.mylink.services;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import ru.mylink.mylink.model.entity.Session;

@Service
@RequiredArgsConstructor
public class TelegramAuthService {

    @Value("${application.externalUrl}")
    private String applicationExternalUrl;

    @Value("${application.centralizedAuthApplicationUrl}")
    private String centralizedAuthAppUrl;

    @Value("${application.centralizedAuthBotUrl}")
    private String centralizedAuthBotUrl;

    private final ObjectMapper mapper;

    private final SessionService sessionService;

    /**
     * Запрашивает авторизационные данные из сервиса централизованной авторизации
     * @param session анонимная сессия текущего пользователя
     * @return идентификатор сессии для авторизации для текущего пользователя
     * @throws JsonProcessingException 
     */
    private String requestUuid() throws JsonProcessingException{
        OkHttpClient client = new OkHttpClient();

        var bodyMap = Map.of(
            "authUrl", applicationExternalUrl + "api/centralized-auth",
            "serviceName", UUID.randomUUID().toString()
        );

        var JSON = MediaType.parse("application/json; charset=utf-8");
        var jsonBody = RequestBody.create(mapper.writeValueAsString(bodyMap), JSON);


        var request = new Request.Builder()
                .url(centralizedAuthAppUrl + "/bot/addInfo")
                .post(jsonBody)
                .build();

        try (var response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (Exception ex) {
            return null;
        }
    }

    public String requestAuthUrl(Session session) throws JsonProcessingException{
        var externalServiceUuid = requestUuid();
        // Сохраняем информацию о том, что пользователь с текущей сессией будет переходить по данной ссылке
        sessionService.putRequest(externalServiceUuid, session.getToken());
        return centralizedAuthBotUrl + "?start=" + externalServiceUuid;
    }

}
