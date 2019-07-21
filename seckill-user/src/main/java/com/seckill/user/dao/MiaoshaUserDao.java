package com.seckill.user.dao;


import com.seckill.common.api.user.vo.MiaoshaUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface MiaoshaUserDao {

    @Select("SELECT * FROM miaosha_user WHERE id = #{id}")
    MiaoshaUser getById(@Param("id") long id);

    @Update("update miaosha_user set password = #{password} where id = #{id}")
    int updatePassword(MiaoshaUser updateUser);
}
