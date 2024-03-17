package edu.java.scrapper.httpClients.gitHub;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.java.scrapper.httpClients.LinkInfo;
import edu.java.scrapper.httpClients.LinkProviderWebService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class GitHubWebClientService extends LinkProviderWebService {
    private static final Pattern REPOSITORY_PATTERN =
        Pattern.compile("https://github.com/(?<owner>.+)/(?<repo>.+)");
    private static final String BASE_URL = "https://api.github.com/repos/";

    public GitHubWebClientService(String baseUrl) {
        super(baseUrl);
    }

    public GitHubWebClientService() {
        super(BASE_URL);
    }

    @Override
    public LinkInfo fetch(URI url) {
        GitHubResponse info = doRequest(url.getPath(), GitHubResponse.class, GitHubResponse.EMPTY_RESPONSE);
        log.info("got info {} from url {}", info, url);

        if (info == null || info.equals(GitHubResponse.EMPTY_RESPONSE)) {
            log.warn("received empty result with link {}", url);
            return null;
        }

        return info.toLinkInfo(url);
    }

    @Override
    public boolean isValid(URI url) {
        return REPOSITORY_PATTERN.matcher(url.toString()).matches();
    }

    private record GitHubResponse(
        long id,
        @JsonProperty("full_name")
        String fullName,
        @JsonProperty("updated_at")
        OffsetDateTime lastModified,
        String description
    ) {
        private static final GitHubResponse EMPTY_RESPONSE =
            new GitHubResponse(0, null, null, null);

        private LinkInfo toLinkInfo(URI url) {
            return new LinkInfo(url, id, fullName, description, lastModified);
        }
    }
}
