DROP TABLE IF EXISTS compilation_has_event;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS compilations;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS statistics;

CREATE TABLE IF NOT EXISTS statistics
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,

    app         VARCHAR(256)                            NOT NULL,
    uri         VARCHAR(2083)                           NOT NULL,
    ip          VARCHAR(256)                            NOT NULL,
    date_create TIMESTAMP                               NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR                                 NOT NULL,
    email VARCHAR                                 NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS compilations
(
    id     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,

    pinned BOOLEAN                                 NOT NULL,
    title  VARCHAR(512)                            NOT NULL,

    CONSTRAINT pk_compilation PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(512)                            NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY (id),
    UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description        TEXT                                    NOT NULL,
    state              VARCHAR(512)                            NOT NULL,
    category_id        BIGINT                                  NOT NULL,
    initiator_id       BIGINT                                  NOT NULL,
    annotation         TEXT                                    NOT NULL,
    title              VARCHAR(512)                            NOT NULL,
    event_date         TIMESTAMP                               NOT NULL,
    published_on       TIMESTAMP,
    lat                float4                                  NOT NULL,
    lon                float4                                  NOT NULL,
    paid               BOOLEAN                                 NOT NULL,
    participant_limit  INTEGER                                 NOT NULL,
    request_moderation BOOLEAN                                 NOT NULL,
    date_create        TIMESTAMP                               NOT NULL,

    CONSTRAINT pk_event PRIMARY KEY (id),
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories,
    CONSTRAINT fk_initiator FOREIGN KEY (initiator_id) REFERENCES users
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id     BIGINT                                  NOT NULL,
    requester_id BIGINT                                  NOT NULL,
    status       VARCHAR(512)                            NOT NULL,
    date_create  TIMESTAMP                               NOT NULL,

    CONSTRAINT pk_request PRIMARY KEY (id),
    CONSTRAINT requester_fk FOREIGN KEY (requester_id) REFERENCES users,
    CONSTRAINT event_fk FOREIGN KEY (event_id) REFERENCES events
);

CREATE TABLE IF NOT EXISTS compilation_has_event
(
    id             BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,

    event_id       BIGINT                                  NOT NULL,
    compilation_id BIGINT                                  NOT NULL,

    CONSTRAINT pk_compilation_event PRIMARY KEY (id),
    CONSTRAINT compilation_fk FOREIGN KEY (compilation_id) REFERENCES compilations ON DELETE CASCADE,
    CONSTRAINT event_fk FOREIGN KEY (event_id) REFERENCES events ON DELETE CASCADE
);