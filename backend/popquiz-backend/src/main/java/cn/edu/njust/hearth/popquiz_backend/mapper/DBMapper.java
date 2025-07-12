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
}
