package edu.java.scrapper.service.jpaService;

import edu.java.scrapper.exceptions.ChatNotFoundException;
import edu.java.scrapper.exceptions.LinkAlreadyTrackedException;
import edu.java.scrapper.exceptions.LinkNotFoundException;
import edu.java.scrapper.httpClients.LinkInfo;
import edu.java.scrapper.model.ControllerDto.AddLinkRequest;
import edu.java.scrapper.model.ControllerDto.LinkResponse;
import edu.java.scrapper.model.ControllerDto.ListLinksResponse;
import edu.java.scrapper.model.ControllerDto.RemoveLinkRequest;
import edu.java.scrapper.model.botClientDto.LinkUpdate;
import edu.java.scrapper.model.domainDto.Link;
import edu.java.scrapper.model.mappers.LinkMapper;
import edu.java.scrapper.repository.jpa.JpaChatRepository;
import edu.java.scrapper.repository.jpa.JpaLinkRepository;
import edu.java.scrapper.repository.jpa.entity.ChatEntity;
import edu.java.scrapper.repository.jpa.entity.LinkEntity;
import edu.java.scrapper.service.LinkService;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public class JpaLinkService implements LinkService {
    private final JpaLinkRepository linkRepository;
    private final JpaChatRepository chatRepository;
    private final LinkMapper mapper;

    public JpaLinkService(JpaLinkRepository linkRepository, JpaChatRepository chatRepository, LinkMapper mapper) {
        this.linkRepository = linkRepository;
        this.chatRepository = chatRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ResponseEntity<ListLinksResponse> getLinks(Long tgChatId) {
        List<LinkResponse> responses = chatRepository.findById(tgChatId)
            .orElseThrow(() -> new ChatNotFoundException(tgChatId))
            .getLinks()
            .stream()
            .map(mapper::linkEntityToLinkResponse)
            .toList();

        log.info("List of links for chat {} was formed", tgChatId);
        return new ResponseEntity<>(
            new ListLinksResponse(
                responses,
                responses.size()
            ),
            HttpStatus.OK
        );
    }

    @Override
    @Transactional
    public ResponseEntity<LinkResponse> addLink(Long tgChatId, AddLinkRequest addLinkRequest) {
        ChatEntity chat = chatRepository.findById(tgChatId)
            .orElseThrow(() -> new ChatNotFoundException(tgChatId));

        LinkEntity link = linkRepository.findByUrl(addLinkRequest.url().toString())
            .orElseGet(() -> linkRepository.save(new LinkEntity(addLinkRequest.url().toString())));

        if (chat.getLinks().contains(link)) {
            log.debug("Chat {} tried to add already tracked link {}", tgChatId, addLinkRequest.url());
            throw new LinkAlreadyTrackedException(addLinkRequest.url());
        }

        chat.addLink(link);
        log.info("Chat {} has added link {}", tgChatId, addLinkRequest.url());
        return new ResponseEntity<>(mapper.linkEntityToLinkResponse(link), HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<LinkResponse> deleteLink(Long tgChatId, RemoveLinkRequest removeLinkRequest) {
        ChatEntity chat = chatRepository.findById(tgChatId)
            .orElseThrow(() -> new ChatNotFoundException(tgChatId));

        Optional<LinkEntity> linkEntity = linkRepository.findByUrl(removeLinkRequest.link().toString());
        if (linkEntity.isEmpty()) {
            log.debug("Chat {} tried to delete not existed link {}", tgChatId, removeLinkRequest.link());
            throw new LinkNotFoundException(removeLinkRequest.link());
        }

        if (!chat.getLinks().contains(linkEntity.get())) {
            log.debug("Chat {} tried to delete untracked link {}", tgChatId, removeLinkRequest.link());
            throw new LinkNotFoundException(removeLinkRequest.link());
        }

        chat.getLinks().remove(linkEntity.get());
        linkEntity.get().getChats().remove(chat);

        //Проверяем, остслеживается ли ссылка другими пользователями, если нет - удаляем
        if (linkEntity.get().getChats().isEmpty()) {
            linkRepository.delete(linkEntity.get());
            log.info("Removing link {}. No one chat track this lisk", removeLinkRequest.link());
        }

        log.info("Chat {} successfully deleted connection with link {}", tgChatId, removeLinkRequest.link());
        return new ResponseEntity<>(mapper.linkEntityToLinkResponse(linkEntity.get()), HttpStatus.OK);
    }

    @Override
    @Transactional
    public LinkUpdate updateLink(LinkInfo update) {
        LinkEntity link = linkRepository.findByUrl(update.url().toString())
            .orElseThrow(() -> new LinkNotFoundException(update.url()));

        link.setLastCheck(OffsetDateTime.now());
        link.setLastUpdate(update.lastModified());

        return new LinkUpdate(
            link.getLinkId(),
            update.url(),
            update.description(),
            link.getChats().stream()
                .map(ChatEntity::getChatId)
                .toList()
        );
    }

    @Override
    public List<Link> getLinksForUpdate(Duration offset) {
        return linkRepository.findByLastCheckBefore(OffsetDateTime.now().minus(offset))
            .stream()
            .map(mapper::linkEntityToLink)
            .toList();
    }
}
