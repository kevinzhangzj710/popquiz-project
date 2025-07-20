package cn.edu.njust.hearth.popquiz_backend.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface DBMapper {
    @Select("""

create table if not exists USERS
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

""")
    public void createUserTable();

    @Select("""

create table if not exists COURSES
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

""")
    public void createCourseTable();

    @Select("""

create table if not exists COURSE_LISTENER
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

""")
    public void createCourseofListenerTable();

    @Select("""

            create table if not exists SPEECHES
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

""")
    public void createSpeechesTable();

    @Select("""

     create table if not exists PUBLIC.QUESTIONS
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

""")
    public void createQUESTIONSTable();


    @Select("""

    create table if not exists PUBLIC.QUESTION_COMMENTS
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

""")
    public void createQUESTION_COMMENTS_Table();


    @Select("""

     create table if not exists PUBLIC.SPEECH_COMMENTS
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

""")
    public void createSPEECH_COMMENTS_Table();


    @Select("""

     create table if not exists PUBLIC.SPEECH_FILES
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

""")
    public void createPEECH_FILES_Table();


    @Select("""

     create table if not exists PUBLIC.SUBMITS
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

""")
    public void createSUBMITSTable();

    @Select("""
            create table if not exists PUBLIC.SPEECH_CONTENT
            (
                ID           INTEGER identity
                    constraint SPEECH_CONTENT_PK
                        primary key,
                SPEECH_ID    INTEGER      not null
                    constraint SPEECH_CONTENT_SPEECHES_ID_FK
                        references PUBLIC.SPEECHES,
                CONTENT_PATH VARCHAR(100) not null
            );
            """)
    public void createSPEECHCONTENTTable();
}
