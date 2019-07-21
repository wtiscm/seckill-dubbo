package com.seckill.user.test.dao;

import com.seckill.user.test.entity.MiaoshaUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MiaoshaUserDao111 {

    @Select("SELECT * FROM miaosha_user WHERE id = #{id}")
    MiaoshaUser getById(@Param("id") long id);

    @Update("update miaosha_user set password = #{password} where id = #{id}")
    int updatePassword(MiaoshaUser updateUser);

    @Update("update miaosha_user set login_count = login_count + 1  where id = #{id}")
    int updateTest(@Param("id") long id);
}
