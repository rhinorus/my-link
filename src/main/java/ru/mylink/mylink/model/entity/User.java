package ru.mylink.mylink.model.entity;

import java.util.Set;

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

    @OneToOne(mappedBy="user")
    private Session session;

    @OneToMany(mappedBy="user")
    private Set<Link> links;

    @OneToOne(mappedBy="user")
    private SessionCentralizedAuthRequest request;

    public User(Long telegramId) {
        setTelegramId(telegramId);
    }

}
