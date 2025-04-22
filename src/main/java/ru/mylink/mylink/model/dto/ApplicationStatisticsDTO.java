package ru.mylink.mylink.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApplicationStatisticsDTO {
    private final Long totalNumberOfUsers;
    private final Long totalNumberOfLinks;
    private final Long totalNumberOfClicks;
}
