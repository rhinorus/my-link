package ru.mylink.mylink.model.entity;

import java.util.Date;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "links",
    indexes = {@Index(columnList = "user_telegram_id")}
)
@Getter @Setter
public class Link {

    @Id
    @Column(length = 128)
    @NotNull
    private String shortUrl;

    @Column(length = 2048)
    @NotNull
    private String url;

    private Boolean isFavorite = false;

    private Long count = 0L;

    @CreationTimestamp
    private Date lastModified;

    // Ссылка может быть привязана как к пользователю, так и к анонимной сессии
    // Привязка к анонимной сессии осуществляется тогда, когда пользователь не авторизован
    @JsonIgnore
    @ManyToOne
    private Session session;

    @JsonIgnore
    @ManyToOne
    private User user;

    public void setShortUrl(String shortUrl){
        this.shortUrl = shortUrl.toLowerCase();
    }

    public void incrementCount(){
        if (Objects.isNull(count))
            count = 0L;
        count++;
    }

    public Long getCount(){
        return Objects.requireNonNullElse(count, 0L);
    }

    public String getUrl(){
        var lower = this.url.toLowerCase();
        if (lower.startsWith("http://") || lower.startsWith("https://"))
            return lower;
        
        return "https://" + lower;
    }
}
