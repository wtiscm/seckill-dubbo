package com.seckill.order.dao;

import com.seckill.common.api.order.domain.MiaoshaOrder;
import com.seckill.common.api.seckill.domain.OrderInfo;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderDao {
    @Select("select * from miaosha_order where user_id = #{userId} and goods_id = #{goodsId}")
    MiaoshaOrder getOrderByUserIdGoodsId(@Param("userId") long userId, @Param("goodsId") long goodsId);

    @Insert("insert into order_info (user_id,goods_id,delivery_addr_id,goods_name,goods_count,goods_price,order_channel,status,create_date)" +
            "values (#{userId},#{goodsId},#{deliveryAddrId},#{goodsName},#{goodsCount},#{goodsPrice},#{orderChannel},#{status},#{createDate})")
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = long.class, before = false, statement = "select last_insert_id()")
    long insertOrder(OrderInfo orderInfo);

    @Insert("insert ignore into miaosha_order (user_id,goods_id,order_id) values (#{userId},#{goodsId},#{orderId})")
    int insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);

    @Select("select * from miaosha_order where order_id = #{orderId}")
    MiaoshaOrder getMiaoshaOrderByOrderId(@Param("orderId") long orderId);

    @Select("select * from order_info where id = #{orderId}")
    OrderInfo getOrderByorderId(@Param("orderId") long orderId);
}
