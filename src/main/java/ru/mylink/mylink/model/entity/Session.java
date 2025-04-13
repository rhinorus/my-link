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
@Table(name = "sessions")
@Getter @Setter
@NoArgsConstructor
public class Session {

    @Id
    private String token;

    @OneToOne
    private User user;

    @OneToMany(mappedBy="session")
    private Set<Link> links;
}
