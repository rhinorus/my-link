package ru.mylink.mylink.model.entity;

import java.util.Date;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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

    // Не преобразуем поле при конвертации в JSON, для безопасности
    @JsonIgnore 
    private Long userId;

    @UpdateTimestamp
    private Date lastModified;

    public void setShortUrl(String shortUrl){
        this.shortUrl = shortUrl.toLowerCase();
    }
}
