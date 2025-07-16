package cn.edu.njust.hearth.popquiz_backend.mapper;
import cn.edu.njust.hearth.popquiz_backend.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM USERS WHERE USERNAME = #{username} AND PASSWORD = #{password}")
    public List<User> findByUsernameAndPassword(
            @Param("username") String username,
            @Param("password") String password
    );

    @Insert("INSERT INTO USERS (USERNAME, PASSWORD, NAME) VALUES (#{user.username},#{user.password},#{user.name})")
    //@Options(useGeneratedKeys = true, keyProperty = "id")
     void register(@Param("user") User user);

}
