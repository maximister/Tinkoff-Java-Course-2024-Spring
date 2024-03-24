package edu.java.scrapper.httpClients.gitHub;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record GitHubEventDto(
    String type,
    EventInfo payload,
    UserInfo actor,
    @JsonProperty("created_at")
    OffsetDateTime updateTime) {
    public record EventInfo(
        String ref,
        IssueInfo issue,
        @JsonProperty("pull_request")
        PullRequestInfo pullRequest,
        String action) {
        public record IssueInfo(String title, @JsonProperty("html_url") String url) {
        }

        public record PullRequestInfo(String title, @JsonProperty("html_url") String url) {
        }
    }

    public record UserInfo(String login) {
    }
}
