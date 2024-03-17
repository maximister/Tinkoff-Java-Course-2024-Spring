package edu.java.scrapper.scheduler;

import edu.java.scrapper.exceptions.UnsupportedLinkException;
import edu.java.scrapper.httpClients.LinkInfo;
import edu.java.scrapper.httpClients.LinkProviderService;
import edu.java.scrapper.httpClients.botClient.BotClient;
import edu.java.scrapper.model.botClientDto.LinkUpdate;
import edu.java.scrapper.model.domainDto.Link;
import edu.java.scrapper.repository.ChatLinkRepository;
import edu.java.scrapper.repository.LinkRepository;
import java.time.Duration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@EnableScheduling
@Slf4j
@Component
public class LinkUpdaterScheduler {
    //Я же правильно понимаю, что это сервисный слой тоже, поэтому
    //обращаться напрямую к репозиторию допустимо
    @Value("#{@'app-edu.java.scrapper.configuration.ApplicationConfig'.scheduler.forceCheckDelay}")
    private Duration forceCheckDelay;
    @Value("#{@'app-edu.java.scrapper.configuration.ApplicationConfig'.scheduler.enable}")
    private boolean isEnable;
    private final BotClient botClient;
    private final List<LinkProviderService> providers;
    private final LinkRepository linkRepository;
    private final ChatLinkRepository chatLinkRepository;


    public LinkUpdaterScheduler(
        BotClient botClient,
        List<LinkProviderService> providers,
        LinkRepository linkRepository,
        ChatLinkRepository chatLinkRepository
    ) {
        this.botClient = botClient;
        this.providers = providers;
        this.linkRepository = linkRepository;
        this.chatLinkRepository = chatLinkRepository;
    }

    @Scheduled(fixedDelayString = "#{@'app-edu.java.scrapper.configuration.ApplicationConfig'.scheduler.interval}")
    @Transactional
    public void update() {
        if (!isEnable) {
            log.warn("Scheduler is unavailable");
            return;
        }

        List<Link> linksForUpdate = linkRepository.findLinksByUpdateTime(forceCheckDelay);

        for (Link link: linksForUpdate) {
            LinkProviderService client = providers.stream()
                .filter(p -> p.isValid(link.url()))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Unsupported link {}. There are no clients for this type", link.url());
                    return new UnsupportedLinkException(link.url());
                });

            LinkInfo update = client.fetch(link.url());
            if (update == null) {
                log.warn("Scheduler has gotten empty answer");
            } else {
                if (update.lastModified().isAfter(link.lastUpdate())) {
                    linkRepository.updateLink(update);
                    List<Long> chats = chatLinkRepository.getChatIdsByLinkId(link.linkId());

                    LinkUpdate botUpdate = new LinkUpdate(
                        link.linkId(),
                        link.url(),
                        "Update request for bot",
                        chats
                    );
                    botClient.sendMessage(botUpdate);
                    log.info("Scheduler has updated link {}", link.url());
                }
            }
        }
    }
}
