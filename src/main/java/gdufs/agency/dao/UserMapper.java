package gdufs.agency.dao;

import gdufs.agency.entity.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface UserMapper {
    int deleteByPrimaryKey(String id);

    int insertSelective(Map map);

    List<User> selectByPrimaryKey();

    int updateByPrimaryKeySelective(User record);

    @Select("select * from `User` where openId=#{openId}")
    User sel(String openId);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(String openId);

    int updateByPrimaryKey(User record);
}