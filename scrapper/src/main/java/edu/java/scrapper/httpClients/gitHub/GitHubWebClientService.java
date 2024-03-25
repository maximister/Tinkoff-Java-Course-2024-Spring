package edu.java.scrapper.httpClients.gitHub;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.java.scrapper.httpClients.LinkInfo;
import edu.java.scrapper.httpClients.LinkProviderWebService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GitHubWebClientService extends LinkProviderWebService {
    private static final Pattern REPOSITORY_PATTERN =
        Pattern.compile("https://github.com/(?<owner>.+)/(?<repo>.+)");
    private static final String BASE_URL = "https://api.github.com/repos/";
    private static final String EVENTS_ENDPOINT = "/events?per_page=10";

    @Value("${client.github.token}")
    private String token;

    public GitHubWebClientService(String baseUrl) {
        super(baseUrl);
    }

    public GitHubWebClientService() {
        super(BASE_URL);
    }

    @Override
    public LinkInfo fetch(URI url) {
        LinkInfo eventsInfo = fetchEvents(url);

        if (eventsInfo != null) {
            log.info("got events info {} from url {}", eventsInfo, url);
            return eventsInfo;
        }

        GitHubResponse info = doRequest(
            url.getPath(),
            GitHubResponse.class,
            GitHubResponse.EMPTY_RESPONSE,
            HttpHeaders.EMPTY
        );
        log.info("got info {} from url {}", info, url);

        if (info == null || info.equals(GitHubResponse.EMPTY_RESPONSE)) {
            log.warn("received empty result with link {}", url);
            return null;
        }

        return info.toLinkInfo(url);
    }

    private LinkInfo fetchEvents(URI url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        GitHubEventDto[] eventsInfo = doRequest(
            url.getPath() + EVENTS_ENDPOINT,
            GitHubEventDto[].class,
            new GitHubEventDto[0],
            HttpHeaders.EMPTY
            //headers
        );

        if (eventsInfo == null || eventsInfo.length == 0) {
            return null;
        }

        return GitHubEventDtoToLinkInfoMapper.getLinkInfo(eventsInfo, url);
    }

    @Override
    public boolean isValid(URI url) {
        return REPOSITORY_PATTERN.matcher(url.toString()).matches();
    }

    private record GitHubResponse(
        @JsonProperty("full_name")
        String fullName,
        @JsonProperty("updated_at")
        OffsetDateTime lastModified,
        String description
    ) {
        private static final GitHubResponse EMPTY_RESPONSE =
            new GitHubResponse(null, null, null);

        private LinkInfo toLinkInfo(URI url) {
            return new LinkInfo(url, fullName, description, lastModified);
        }
    }

    private final static class GitHubEventDtoToLinkInfoMapper {

        private GitHubEventDtoToLinkInfoMapper() {
        }

        static LinkInfo getLinkInfo(GitHubEventDto[] events, URI url) {
            StringBuilder message = new StringBuilder();
            Set<String> eventTypes = Arrays
                .stream(GitHubEventType.values())
                .map(GitHubEventType::getType)
                .collect(Collectors.toSet());

            OffsetDateTime lastUpdate = OffsetDateTime.MIN;

            for (GitHubEventDto event : events) {
                if (eventTypes.contains(event.type())) {
                    message.append(GitHubEventType.getGitHubEventMessage(event));
                    if (lastUpdate.isBefore(event.updateTime())) {
                        lastUpdate = event.updateTime();
                    }
                }
            }
            log.debug(message.toString());

            return new LinkInfo(
                url,
                "GitHub update",
                message.toString(),
                lastUpdate
            );

        }
    }
}
