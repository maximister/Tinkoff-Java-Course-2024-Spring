package edu.java.scrapper.configuration;

import edu.java.scrapper.model.mappers.LinkMapper;
import edu.java.scrapper.repository.jdbcRepository.JdbcChatLinkRepository;
import edu.java.scrapper.repository.jdbcRepository.JdbcChatRepository;
import edu.java.scrapper.repository.jdbcRepository.JdbcLinkRepository;
import edu.java.scrapper.repository.jooqRepository.JooqChatLinkRepository;
import edu.java.scrapper.repository.jooqRepository.JooqChatRepository;
import edu.java.scrapper.repository.jooqRepository.JooqLinkRepository;
import edu.java.scrapper.repository.jpa.JpaChatRepository;
import edu.java.scrapper.repository.jpa.JpaLinkRepository;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.TgChatService;
import edu.java.scrapper.service.jdbcService.JdbcChatService;
import edu.java.scrapper.service.jdbcService.JdbcLinkService;
import edu.java.scrapper.service.jooqService.JooqChatService;
import edu.java.scrapper.service.jooqService.JooqLinkService;
import edu.java.scrapper.service.jpaService.JpaChatService;
import edu.java.scrapper.service.jpaService.JpaLinkService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {
    @Bean
    @ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
    public TgChatService jdbcChatService(JdbcChatRepository jdbcChatRepository) {
        return new JdbcChatService(jdbcChatRepository);
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
    public LinkService jdbcLinkService(
        JdbcLinkRepository linkRepository,
        JdbcChatLinkRepository chatLinkRepository,
        JdbcChatRepository chatRepository,
        LinkMapper mapper
    ) {
        return new JdbcLinkService(linkRepository, chatLinkRepository, chatRepository, mapper);
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
    public TgChatService jooqChatService(JooqChatRepository chatRepository) {
        return new JooqChatService(chatRepository);
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
    public LinkService jooqLinkService(
        JooqLinkRepository linkRepository,
        JooqChatLinkRepository chatLinkRepository,
        JooqChatRepository chatRepository,
        LinkMapper mapper
        ) {
        return new JooqLinkService(linkRepository, chatLinkRepository, chatRepository, mapper);
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
    public TgChatService jpaChatService(JpaChatRepository chatRepository) {
        return new JpaChatService(chatRepository);
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
    public LinkService jpaLinkService(
        JpaChatRepository chatRepository,
        JpaLinkRepository linkRepository,
        LinkMapper mapper
    ) {
        return new JpaLinkService(linkRepository, chatRepository, mapper);
    }

}
