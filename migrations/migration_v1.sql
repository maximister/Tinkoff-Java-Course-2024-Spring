CREATE TABLE link
(
    link_id     bigint                   NOT NULL GENERATED ALWAYS AS IDENTITY,
    last_update timestamp with time zone NOT NULL,
    url         text                     NOT NULL,
    PRIMARY KEY (link_id)
);

CREATE TABLE chat
(
    chat_id bigint NOT NULL,
    PRIMARY KEY (chat_id)
);

CREATE TABLE chat_link
(
    chat_id BIGINT,
    link_id BIGINT,
    FOREIGN KEY (chat_id) REFERENCES chat (chat_id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (link_id) REFERENCES link (link_id) ON UPDATE CASCADE ON DELETE CASCADE,
    PRIMARY KEY (chat_id, link_id)
);
