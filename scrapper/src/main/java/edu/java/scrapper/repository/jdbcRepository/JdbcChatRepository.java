package edu.java.scrapper.repository.jdbcRepository;

import edu.java.scrapper.model.domainDto.Chat;
import edu.java.scrapper.repository.ChatRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class JdbcChatRepository implements ChatRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ChatRowMapper mapper;
    private static final String ID = "chat_id";

    public JdbcChatRepository(JdbcTemplate jdbcTemplate, ChatRowMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    @Override
    public void addChat(long chatId) {
        String query = "INSERT INTO chat VALUES(?)";
        jdbcTemplate.update(query, chatId);
    }

    @Override
    public void deleteChat(long chatId) {
        String query = "DELETE FROM chat WHERE chat_id = ?";
        jdbcTemplate.update(query, chatId);
    }

    @Override
    public List<Chat> getAllChats() {
        String query = "SELECT * FROM chat";
        return jdbcTemplate.query(query, mapper);
    }

    @Override
    public Chat findChatById(long chatId) {
        String query = "SELECT * FROM chat WHERE chat_id = ?";
        return jdbcTemplate.query(query, mapper, chatId).stream().findAny().orElse(null);
    }

    @Component
    public static class ChatRowMapper implements RowMapper<Chat> {
        @Override
        public Chat mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Chat(rs.getLong(ID));
        }
    }
}
