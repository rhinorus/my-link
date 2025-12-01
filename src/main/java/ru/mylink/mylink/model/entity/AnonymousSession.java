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
@Table(name = "anonymous_sessions")
@Getter @Setter
@NoArgsConstructor
public class AnonymousSession {

    @Id
    private String token;

    @OneToMany(mappedBy="anonymousSession")
    private Set<Link> links;
}
