package com.seckill.miaosha.dao;

import com.seckill.common.api.goods.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GoodsDao {

    @Select("select g.*,mg.miaosha_price,mg.stock_count,mg.start_date,mg.end_date from miaosha_goods mg left join goods g on mg.goods_id = g.id")
    List<GoodsVo> listGoodsVo();

    @Select("select g.*,mg.miaosha_price,mg.stock_count,mg.start_date,mg.end_date from miaosha_goods mg left join goods g on mg.goods_id = g.id where mg.goods_id = #{goodId}")
    GoodsVo getGoodsVoById(@Param("goodId") long goodId);

    @Select("select stock_count from miaosha_goods where goods_id = #{goodsId}")
    int getStockByGoodsId(@Param("goodsId") long goodsId);

    @Update("update miaosha_goods set stock_count = stock_count - 1 where goods_id = #{miaoshaGoodsId} and stock_count > 0")
    int redurceStock(@Param("miaoshaGoodsId") long miaoshaGoodsId);
}
