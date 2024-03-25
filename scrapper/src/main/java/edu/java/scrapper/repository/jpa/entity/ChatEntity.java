package edu.java.scrapper.repository.jpa.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "chat")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatEntity {
    @Id
    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
        name = "chat_link",
        joinColumns = @JoinColumn(name = "chat_id"),
        inverseJoinColumns = @JoinColumn(name = "link_id"))
    private Set<LinkEntity> links = new HashSet<>();

    public ChatEntity(Long chatId) {
        this.chatId = chatId;
    }

    public void addLink(LinkEntity linkEntity) {
        links.add(linkEntity);
        linkEntity.getChats().add(this);
    }
}
