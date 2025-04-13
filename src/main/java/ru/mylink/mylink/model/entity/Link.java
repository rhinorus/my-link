package ru.mylink.mylink.model.entity;

import java.util.Date;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "links",
    indexes = {@Index(columnList = "userId")}
)
@Getter @Setter @NoArgsConstructor
public class Link {

    @Id
    @Column(length = 128)
    @NotNull
    private String shortUrl;

    @Column(length = 2048)
    @NotNull
    private String url;

    @UpdateTimestamp
    private Date lastModified;

    // Ссылка может быть привязана как к пользователю, так и к анонимной сессии
    // Привязка к анонимной сессии осуществляется тогда, когда пользователь не авторизован
    @ManyToOne
    private Session session;

    @ManyToOne
    private User user;

    public void setShortUrl(String shortUrl){
        this.shortUrl = shortUrl.toLowerCase();
    }
}
