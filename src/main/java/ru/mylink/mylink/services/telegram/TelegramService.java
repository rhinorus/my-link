package ru.mylink.mylink.services.telegram;

import java.util.Map;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


/** Функции, связанные с Telegram */
@Log4j2
@Service
public class TelegramService {

    @Value(value = "${telegram.token}")
    String botToken;
    String ALGORITHM = "HmacSHA256";
    
    /** Валидация dataCheck строки. Используется в качестве защиты от подмены данных. <br>
     *
     * @param dataCheck строка для валидации, создается на основе полученных от Telegram данных в {@link #buildDataCheckString(Map) }
     * @param hash проверочная строка, сгенерированная на стороне Telegram
     * @return true, если данные получены от Telegram. false, если строка скомпрометирована.
     * 
     * @see <a href="https://core.telegram.org/bots/webapps#validating-data-received-via-the-mini-app">Официальное описание документации</a>
     */
    public Boolean isValid(String dataCheck, String hash) {
        try {
            Mac sha256_HMAC = Mac.getInstance(ALGORITHM);

            // В качестве секретного ключа используется TelegramBotToken, хэшированный с константой WebAppData
            var constantKey = new SecretKeySpec("WebAppData".getBytes(), ALGORITHM);
            sha256_HMAC.init(constantKey);
            var encodedKey = sha256_HMAC.doFinal(botToken.getBytes());
            var secretKey = new SecretKeySpec(encodedKey, ALGORITHM);

            sha256_HMAC.init(secretKey);
            var encoded = sha256_HMAC.doFinal(dataCheck.getBytes());
            var hex = byteArrayToHex(encoded);

            return hex.equals(hash);

        } catch (Exception ex) {
            log.error("Алгоритм HMAC_SHA256 не поддерживается!");
            return false;
        }
    }

    private static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    /** Строит dataCheck строку из параметров, полученных от Telegram.
     * 
     * @param parameters набор параметров в виде ключ-значение
     * @return строку dataCheck
     * 
     * @see <a href="https://core.telegram.org/bots/webapps#validating-data-received-via-the-mini-app">Официальное описание построения строки dataCheck </a>
     */
    public String buildDataCheckString(Map<String, String[]> parameters){

        var sortedKeys = parameters.keySet()
            .stream()
            .filter(x -> !x.equals("hash")) // обязательно нужно убрать из параметров. В документации не указано, подобрано эмпирически.
            .sorted()
            .toList();

        var parameterRows = new ArrayList<String>();

        for(var key : sortedKeys){
            parameterRows.add(key + "=" + String.join("", parameters.get(key)));
        }

        return String.join("\n", parameterRows);
    }

}
