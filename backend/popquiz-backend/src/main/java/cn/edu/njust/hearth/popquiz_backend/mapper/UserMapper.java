package cn.edu.njust.hearth.popquiz_backend.mapper;
import cn.edu.njust.hearth.popquiz_backend.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM USERS WHERE USERNAME = #{username} AND PASSWORD = #{password}")
    public List<User> findByUsernameAndPassword(
            @Param("username") String username,
            @Param("password") String password
    );

    @Insert("INSERT INTO USERS (USERNAME, PASSWORD, NAME) VALUES (#{user.username},#{user.password},#{user.name})")
    void register(@Param("user") User user);

}
