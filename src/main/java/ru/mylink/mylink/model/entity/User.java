package ru.mylink.mylink.model.entity;

import java.util.Set;

import com.oauth0.lib.dto.response.UserDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    private Long telegramId;

    private String username;

    @OneToMany(mappedBy="user")
    private Set<Link> links;

    public User(Long telegramId) {
        setTelegramId(telegramId);
    }

    public User(UserDTO userDTO) {
        this.telegramId = userDTO.getId();
        this.username = userDTO.getUsername();
    }
}
