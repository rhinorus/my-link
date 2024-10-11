package ru.mylink.mylink.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    private Long id;
    private String username;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @Override
    public String toString() {
        return String.format(
            "id: %s, username: %s, firstName: %s, lastName: %s",
            getId(),
            getUsername(),
            getFirstName(),
            getLastName()  
        );
    }

}
