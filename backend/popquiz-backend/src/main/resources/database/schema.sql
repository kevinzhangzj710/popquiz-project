create table PUBLIC.USERS
(
    ID       INTEGER identity
        constraint USERS_PK
            primary key,
    USERNAME VARCHAR(30) not null
        constraint USERS_PK_2
            unique,
    PASSWORD VARCHAR(64) not null,
    NAME     VARCHAR(30) not null
);

create table PUBLIC.COURSES
(
    ID           INTEGER identity
        constraint COURSES_PK
            primary key,
    TITLE        VARCHAR(60)             not null,
    DESCRIPTION  VARCHAR(200) default '' not null,
    ORGANIZER_ID INTEGER                 not null
        constraint COURSES_USERS_ID_FK
            references PUBLIC.USERS
);

create table PUBLIC.COURSE_LISTENER
(
    ID        INTEGER identity
        constraint COURSE_LISTENER_PK
            primary key,
    COURSE_ID INTEGER not null
        constraint COURSE_LISTENER_COURSES_ID_FK
            references PUBLIC.COURSES,
    USER_ID   INTEGER not null
        constraint COURSE_LISTENER_USERS_ID_FK
            references PUBLIC.USERS
);

create table PUBLIC.SPEECHES
(
    ID         INTEGER identity
        constraint SPEECHES_PK
            primary key,
    TITLE      VARCHAR(60) not null,
    SPEAKER_ID INTEGER     not null
        constraint SPEECHES_USERS_ID_FK
            references PUBLIC.USERS,
    COURSE_ID  INTEGER     not null
        constraint SPEECHES_COURSES_ID_FK
            references PUBLIC.COURSES
);

create table PUBLIC.QUESTIONS
(
    ID         INTEGER identity
        constraint QUESTIONS_PK
            primary key,
    QUESTION   VARCHAR(200)                        not null,
    SELECTION  VARCHAR(200)                        not null,
    ANSWER     CHARACTER(1)                        not null,
    SPEECH_ID  INTEGER                             not null
        constraint QUESTIONS_SPEECHES_ID_FK
            references PUBLIC.SPEECHES,
    START_TIME TIMESTAMP default CURRENT_TIMESTAMP not null,
    END_TIME   TIMESTAMP                           not null
);

create table PUBLIC.QUESTION_COMMENTS
(
    ID          INTEGER identity
        constraint QUESTION_COMMENTS_PK
            primary key,
    COMMENT     VARCHAR(100) not null,
    QUESTION_ID INTEGER      not null
        constraint QUESTION_COMMENTS_QUESTIONS_ID_FK
            references PUBLIC.QUESTIONS,
    USER_ID     INTEGER      not null
        constraint QUESTION_COMMENTS_USERS_ID_FK
            references PUBLIC.USERS
);

create table PUBLIC.SPEECH_COMMENTS
(
    ID        INTEGER identity
        constraint SPEECH_COMMENTS_PK
            primary key,
    COMMENT   VARCHAR(100) not null,
    SPEECH_ID INTEGER      not null
        constraint SPEECH_COMMENTS_SPEECHES_ID_FK
            references PUBLIC.SPEECHES,
    USER_ID   INTEGER      not null
        constraint SPEECH_COMMENTS_USERS_ID_FK
            references PUBLIC.USERS
);

create table PUBLIC.SPEECH_FILES
(
    ID        INTEGER identity
        constraint SPEECH_FILES_PK
            primary key,
    FILENAME  VARCHAR(200) not null,
    FILEPATH  VARCHAR(100) not null,
    SPEECH_ID INTEGER      not null
        constraint SPEECH_FILES_SPEECHES_ID_FK
            references PUBLIC.SPEECHES
);

create table PUBLIC.SUBMITS
(
    ID          INTEGER identity
        constraint SUBMITS_PK
            primary key,
    QUESTION_ID INTEGER      not null
        constraint SUBMITS_QUESTIONS_ID_FK
            references PUBLIC.QUESTIONS,
    USER_ID     INTEGER      not null
        constraint SUBMITS_USERS_ID_FK
            references PUBLIC.USERS,
    ANSWER      CHARACTER(1) not null
);