package ru.mylink.mylink.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
public class SessionCentralizedAuthRequest {

    @Id
    private String uuid;

    @OneToOne
    private Session session;

    @OneToOne
    private User user;

    public SessionCentralizedAuthRequest(String uuid, Session session) {
        setUuid(uuid);
        setSession(session);
    }

}
