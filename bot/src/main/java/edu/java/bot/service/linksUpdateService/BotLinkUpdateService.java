package edu.java.bot.service.linksUpdateService;

import edu.java.bot.model.controllerDto.LinkUpdate;
import org.springframework.http.ResponseEntity;

public class BotLinkUpdateService implements LinksUpdateService {
    @Override
    public ResponseEntity<Void> updateLink(LinkUpdate linkUpdateRequest) {
        return null;
    }
}
