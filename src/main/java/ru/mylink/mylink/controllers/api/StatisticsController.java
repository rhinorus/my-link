package ru.mylink.mylink.controllers.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.mylink.mylink.model.dto.ApplicationStatisticsDTO;
import ru.mylink.mylink.services.StatisticsService;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/stats")
public class StatisticsController {
    
    private final StatisticsService statisticsService;

    @GetMapping
    public ApplicationStatisticsDTO get() {
        return statisticsService.getStatistics();
    }
    

}
