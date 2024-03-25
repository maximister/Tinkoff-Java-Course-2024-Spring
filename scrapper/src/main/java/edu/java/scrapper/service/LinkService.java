package edu.java.scrapper.service;

import edu.java.scrapper.httpClients.LinkInfo;
import edu.java.scrapper.model.ControllerDto.AddLinkRequest;
import edu.java.scrapper.model.ControllerDto.LinkResponse;
import edu.java.scrapper.model.ControllerDto.ListLinksResponse;
import edu.java.scrapper.model.ControllerDto.RemoveLinkRequest;
import edu.java.scrapper.model.botClientDto.LinkUpdate;
import edu.java.scrapper.model.domainDto.Link;
import java.time.Duration;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface LinkService {
    ResponseEntity<ListLinksResponse> getLinks(Long tgChatId);

    ResponseEntity<LinkResponse> addLink(Long tgChatId, AddLinkRequest addLinkRequest);

    ResponseEntity<LinkResponse> deleteLink(Long tgChatId, RemoveLinkRequest removeLinkRequest);

    LinkUpdate updateLink(LinkInfo update);

    List<Link> getLinksForUpdate(Duration offset);
}
