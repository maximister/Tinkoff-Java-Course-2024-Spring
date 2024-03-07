package edu.java.bot.service.linksUpdateService;

import edu.java.bot.model.controllerDto.LinkUpdate;
import org.springframework.http.ResponseEntity;

public interface LinksUpdateService {
    ResponseEntity<Void> updateLink(LinkUpdate linkUpdateRequest);
}
