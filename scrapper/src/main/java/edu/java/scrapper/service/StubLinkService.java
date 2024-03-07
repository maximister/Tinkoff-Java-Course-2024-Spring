package edu.java.scrapper.service;

import edu.java.scrapper.model.AddLinkRequest;
import edu.java.scrapper.model.LinkResponse;
import edu.java.scrapper.model.ListLinksResponse;
import edu.java.scrapper.model.RemoveLinkRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class StubLinkService implements LinkService {
    @Override
    public ResponseEntity<ListLinksResponse> getLinks(Long id) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    @Override
    public ResponseEntity<LinkResponse> addLink(Long tgChatId, AddLinkRequest addLinkRequest) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<LinkResponse> deleteLink(Long tgChatId, RemoveLinkRequest removeLinkRequest) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
