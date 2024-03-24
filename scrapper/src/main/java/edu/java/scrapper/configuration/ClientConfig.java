package edu.java.scrapper.configuration;

import edu.java.scrapper.httpClients.botClient.BotClient;
import edu.java.scrapper.httpClients.botClient.WebBotClient;
import edu.java.scrapper.httpClients.gitHub.GitHubWebClientService;
import edu.java.scrapper.httpClients.stackOverflow.StackOverflowWebClientService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {
    @Bean
    public BotClient botClient() {
        return new WebBotClient();
    }

    @Bean
    public GitHubWebClientService gitHubWebClientService() {
        return new GitHubWebClientService();
    }

    @Bean
    public StackOverflowWebClientService stackOverflowWebClientService() {
        return new StackOverflowWebClientService();
    }
}
