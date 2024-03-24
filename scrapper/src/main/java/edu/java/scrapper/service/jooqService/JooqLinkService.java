package edu.java.scrapper.service.jooqService;

import edu.java.scrapper.exceptions.ChatNotFoundException;
import edu.java.scrapper.exceptions.LinkAlreadyTrackedException;
import edu.java.scrapper.exceptions.LinkNotFoundException;
import edu.java.scrapper.model.ControllerDto.AddLinkRequest;
import edu.java.scrapper.model.ControllerDto.LinkResponse;
import edu.java.scrapper.model.ControllerDto.ListLinksResponse;
import edu.java.scrapper.model.ControllerDto.RemoveLinkRequest;
import edu.java.scrapper.model.domainDto.Link;
import edu.java.scrapper.model.mappers.LinkMapper;
import edu.java.scrapper.repository.ChatLinkRepository;
import edu.java.scrapper.repository.ChatRepository;
import edu.java.scrapper.repository.LinkRepository;
import edu.java.scrapper.service.LinkService;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("JdbcLinkJooqLinkService")
@Slf4j
public class JooqLinkService implements LinkService {
    private final LinkRepository linkRepository;
    private final ChatLinkRepository chatLinkRepository;
    private final ChatRepository chatRepository;
    private final LinkMapper mapper;

    public JooqLinkService(
        @Qualifier("JooqLinkRepository")
        LinkRepository linkRepository,
        @Qualifier("JooqChatLinkRepository")
        ChatLinkRepository repository,
        @Qualifier("JooqChatRepository")
        ChatRepository chatRepository,
        LinkMapper mapper
    ) {
        this.linkRepository = linkRepository;
        this.chatLinkRepository = repository;
        this.chatRepository = chatRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ResponseEntity<ListLinksResponse> getLinks(Long tgChatId) {
        checkChat(tgChatId);

        List<Long> linksIds = chatLinkRepository.getLinkIdsByChatId(tgChatId);
        List<Link> links = null;
        if (!linksIds.isEmpty()) {
            links = linkRepository.getLinks(linksIds);
        } else {
            links = Collections.emptyList();
        }

        log.info("List of links for chat {} was formed", tgChatId);
        return new ResponseEntity<>(
            new ListLinksResponse(
                links.stream().map(mapper::linkToLinkResponse).toList(),
                links.size()
            ),
            HttpStatus.OK
        );
    }

    @Override
    @Transactional
    public ResponseEntity<LinkResponse> addLink(Long tgChatId, AddLinkRequest addLinkRequest) {
        checkChat(tgChatId);
        Link resultLink = linkRepository.findLinkByUrl(addLinkRequest.url());
        if (resultLink == null) {
            resultLink = linkRepository.addLink(addLinkRequest.url());
        }

        try {
            chatLinkRepository.addChatLinkConnection(tgChatId, resultLink.linkId());
        } catch (DataAccessException e) {
            log.debug("Chat {} tried to add already tracked link {}", tgChatId, addLinkRequest.url());
            throw new LinkAlreadyTrackedException(addLinkRequest.url());
        }

        log.info("Chat {} has added link {}", tgChatId, addLinkRequest.url());
        return new ResponseEntity<>(mapper.linkToLinkResponse(resultLink), HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<LinkResponse> deleteLink(Long tgChatId, RemoveLinkRequest removeLinkRequest) {
        checkChat(tgChatId);

        Link link = linkRepository.findLinkByUrl(removeLinkRequest.link());
        if (link == null) {
            log.debug("Chat {} tried to delete not existed link {}", tgChatId, removeLinkRequest.link());
            throw new LinkNotFoundException(removeLinkRequest.link());
        }

        try {
            chatLinkRepository.deleteChatLinkConnection(tgChatId, link.linkId());
        } catch (DataAccessException e) {
            log.debug("Chat {} tried to delete untracked link {}", tgChatId, removeLinkRequest.link());
            throw new LinkNotFoundException(removeLinkRequest.link());
        }

        //Проверяем, остслеживается ли ссылка другими пользователями, если нет - удаляем
        if (!chatLinkRepository.isLinkTracked(link.linkId())) {
            linkRepository.deleteLink(link.linkId());
            log.info("Removing link {}. No one chat track this lisk", removeLinkRequest.link());
        }

        log.info("Chat {} successfully deleted connection with link {}", tgChatId, removeLinkRequest.link());
        return new ResponseEntity<>(mapper.linkToLinkResponse(link), HttpStatus.OK);
    }

    private void checkChat(Long tgChatId) {
        if (chatRepository.findChatById(tgChatId) == null) {
            log.debug("Chat with id {} was not found", tgChatId);
            throw new ChatNotFoundException(tgChatId);
        }
    }
}

