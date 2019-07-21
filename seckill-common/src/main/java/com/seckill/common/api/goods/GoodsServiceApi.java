package com.seckill.common.api.goods;

import com.seckill.common.api.goods.vo.GoodsVo;

import java.util.List;

public interface GoodsServiceApi {
    public List<GoodsVo> listGoodsVo();
    public GoodsVo getGoodsVoById(long goodId);
    public int getStockByGoodsId(long goodsId);
    public Boolean redurceStock(GoodsVo goods);
}
