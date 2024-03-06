package edu.java.configuration;

import edu.java.httpClients.gitHub.GitHubWebClientService;
import edu.java.httpClients.stackOverflow.StackOverflowWebClientService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {
    @Bean
    public GitHubWebClientService gitHubWebClientService() {
        return new GitHubWebClientService();
    }

    @Bean
    public StackOverflowWebClientService stackOverflowWebClientService() {
        return new StackOverflowWebClientService();
    }
}
