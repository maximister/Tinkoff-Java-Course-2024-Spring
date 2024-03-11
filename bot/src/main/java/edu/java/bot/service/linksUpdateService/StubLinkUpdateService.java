package edu.java.bot.service.linksUpdateService;

import edu.java.bot.model.controllerDto.LinkUpdate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class StubLinkUpdateService implements LinksUpdateService {
    @Override
    public ResponseEntity<Void> updateLink(LinkUpdate linkUpdateRequest) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
