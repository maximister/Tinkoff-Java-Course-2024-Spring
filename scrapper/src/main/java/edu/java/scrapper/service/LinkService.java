package edu.java.scrapper.service;

import edu.java.scrapper.model.ControllerDto.AddLinkRequest;
import edu.java.scrapper.model.ControllerDto.LinkResponse;
import edu.java.scrapper.model.ControllerDto.ListLinksResponse;
import edu.java.scrapper.model.ControllerDto.RemoveLinkRequest;
import org.springframework.http.ResponseEntity;

public interface LinkService {
    ResponseEntity<ListLinksResponse> getLinks(Long tgChatId);

    ResponseEntity<LinkResponse> addLink(Long tgChatId, AddLinkRequest addLinkRequest);

    ResponseEntity<LinkResponse> deleteLink(Long tgChatId, RemoveLinkRequest removeLinkRequest);
}
