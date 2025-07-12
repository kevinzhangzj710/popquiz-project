-- we don't know how to generate root <with-no-name> (class Root) :(

create table USERS
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

create table COURSES
(
    ID           INTEGER identity
        constraint COURSES_PK
            primary key,
    TITLE        VARCHAR(60)             not null,
    DESCRIPTION  VARCHAR(200) default '' not null,
    ORGANIZER_ID INTEGER                 not null
        constraint COURSES_USERS_ID_FK
            references USERS
);

create table COURSE_LISTENER
(
    ID        INTEGER identity
        constraint COURSE_LISTENER_PK
            primary key,
    COURSE_ID INTEGER not null
        constraint COURSE_LISTENER_COURSES_ID_FK
            references COURSES,
    USER_ID   INTEGER not null
        constraint COURSE_LISTENER_USERS_ID_FK
            references USERS
);

create table SPEECHES
(
    ID         INTEGER identity
        constraint SPEECHES_PK
            primary key,
    TITLE      VARCHAR(60) not null,
    SPEAKER_ID INTEGER     not null
        constraint SPEECHES_USERS_ID_FK
            references USERS,
    COURSE_ID  INTEGER     not null
        constraint SPEECHES_COURSES_ID_FK
            references COURSES
);

