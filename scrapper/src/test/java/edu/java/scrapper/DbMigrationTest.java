package edu.java.scrapper;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.PreparedStatement;
import static org.assertj.core.api.Assertions.assertThat;

public class DbMigrationTest extends IntegrationEnvironment {
    @Test
    @DisplayName("Chat table test")
    @SneakyThrows
    public void chatTest() {
        Connection connection = POSTGRES.createConnection("");
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM chat");
        assertThat(statement.executeQuery().getMetaData().getColumnName(1)).isEqualTo("chat_id");
    }

    @Test
    @DisplayName("Link table test")
    @SneakyThrows
    public void linkTest() {
        Connection connection = POSTGRES.createConnection("");
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM link");
        var meta = statement.executeQuery().getMetaData();
        assertThat(meta.getColumnName(1)).isEqualTo("link_id");
        assertThat(meta.getColumnName(2)).isEqualTo("last_update");
        assertThat(meta.getColumnName(3)).isEqualTo("url");
    }
}
