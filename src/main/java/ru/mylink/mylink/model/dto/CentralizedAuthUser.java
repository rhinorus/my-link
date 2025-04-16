package ru.mylink.mylink.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class CentralizedAuthUser {
    private Long id;
    private String uuid;
    private String username = "rhinorus";
}
