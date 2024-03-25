package edu.java.scrapper.repository.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "link")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class LinkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "link_id")
    private Long linkId;

    @Column(name = "last_update")
    private OffsetDateTime lastUpdate = OffsetDateTime.now();

    @SuppressWarnings("checkstyle:MagicNumber")
    @Column(name = "last_check")
    private OffsetDateTime lastCheck = OffsetDateTime.now();

    @Column(nullable = false, unique = true)
    private String url;

    @ManyToMany(mappedBy = "links", fetch = FetchType.LAZY)
    private Set<ChatEntity> chats = new HashSet<>();

    public LinkEntity(String url) {
        this.url = url;
    }
}
