package edu.java.httpClients.stackOverflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.java.httpClients.LinkInfo;
import edu.java.httpClients.LinkProviderWebService;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class StackOverflowWebClientService extends LinkProviderWebService {
    private static final Pattern STACKOVERFLOW_PATTERN =
        Pattern.compile("https://stackoverflow.com/questions/(\\d+).*");
    private static final String BASE_URL = "https://api.stackexchange.com/2.3";

    public StackOverflowWebClientService(String baseUrl) {
        super(baseUrl);
    }

    public StackOverflowWebClientService() {
        super(BASE_URL);
    }

    @Override
    public LinkInfo fetch(URL url) {
        if (!isValid(url)) {
            log.warn("URL {} is invalid", url);
            return null;
        }

        Matcher matcher = STACKOVERFLOW_PATTERN.matcher(url.toString());
        matcher.matches();
        String id = matcher.group(1);
        StackOverflowRequest info =
            doRequest(
                "/questions/" + id + "?site=stackoverflow",
                StackOverflowRequest.class,
                StackOverflowRequest.EMPTY_RESPONSE
            );
        log.info("info {} from url {}", info, url.toString());

        if (info == null || info.equals(StackOverflowRequest.EMPTY_RESPONSE)) {
            return null;
        }

        return info.toLinkInfo(url);
    }

    @Override
    protected boolean isValid(URL url) {
        return STACKOVERFLOW_PATTERN.matcher(url.toString()).matches();
    }

    public record StackOverflowRequest(List<StackOverflowItem> items) {
        private static final StackOverflowRequest EMPTY_RESPONSE =
            new StackOverflowRequest(null);

        private LinkInfo toLinkInfo(URL url) {
            StackOverflowItem item = items.get(0);
            String description = new StringBuilder()
                .append("view count: ")
                .append(item.viewCount)
                .append('\n')
                .append("answer count: ")
                .append(item.answerCount)
                .toString();

            return new LinkInfo(
                url,
                item.id,
                item.title,
                description,
                item.lastModified
            );
        }

        private record StackOverflowItem(
            @JsonProperty("question_id")
            Long id,
            @JsonProperty("last_activity_date")
            OffsetDateTime lastModified,
            String title,
            @JsonProperty("view_count")
            Integer viewCount,
            @JsonProperty("answer_count")
            Integer answerCount
        ) {
        }
    }
}
