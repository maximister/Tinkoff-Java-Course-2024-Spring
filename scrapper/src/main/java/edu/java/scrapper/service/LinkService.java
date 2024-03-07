package edu.java.scrapper.service;

import edu.java.scrapper.model.AddLinkRequest;
import edu.java.scrapper.model.LinkResponse;
import edu.java.scrapper.model.ListLinksResponse;
import edu.java.scrapper.model.RemoveLinkRequest;
import org.springframework.http.ResponseEntity;

public interface LinkService {
    ResponseEntity<ListLinksResponse> getLinks(Long id);

    ResponseEntity<LinkResponse> addLink(Long tgChatId, AddLinkRequest addLinkRequest);

    ResponseEntity<LinkResponse> deleteLink(Long tgChatId, RemoveLinkRequest removeLinkRequest);
}
