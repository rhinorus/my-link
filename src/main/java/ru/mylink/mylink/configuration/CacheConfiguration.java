package ru.mylink.mylink.configuration;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@EnableCaching
@Configuration
@SuppressWarnings("all")
public class CacheConfiguration {

    @Bean
    public Caffeine caffeineConfiguration() {
        return Caffeine.newBuilder().expireAfterWrite(
            60, TimeUnit.SECONDS  
        );
    }

    @Bean
    public CacheManager cacheManagerConfiguration(Caffeine caffeine) {
        CaffeineCacheManager manager = new CaffeineCacheManager();
        manager.setCaffeine(caffeine);
        return manager;
    }
    
}
