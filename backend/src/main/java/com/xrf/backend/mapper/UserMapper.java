package com.xrf.backend.mapper;

import com.xrf.backend.pojo.User;
import com.xrf.backend.dto.UserDTO;
import com.xrf.backend.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    //注册
    User findByUsername(String username);
    int insert(User user);

    //获取当前用户信息
    @Select("select * from users where id = #{id}")
    UserVO getById(Integer id);

    //修改用户
    int updateUser(UserDTO userDTO);

}
