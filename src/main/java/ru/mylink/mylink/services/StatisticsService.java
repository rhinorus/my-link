package ru.mylink.mylink.services;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.mylink.mylink.model.dto.ApplicationStatisticsDTO;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final UserService userService;
    private final LinkService linkService;
    
    // Метод кэшируется и обновляется только один раз
    // за определенный период
    @Cacheable(value = "statistics")
    public ApplicationStatisticsDTO getStatistics(){

        var totalUsers = userService.count();
        var totalLinks = linkService.count();
        var totalClicks = linkService.totalClicks();

        return new ApplicationStatisticsDTO(
            totalUsers,
            totalLinks,
            totalClicks
        );
    }

}
