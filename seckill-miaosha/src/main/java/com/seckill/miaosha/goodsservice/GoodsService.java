package com.seckill.miaosha.goodsservice;

import com.seckill.common.api.goods.GoodsServiceApi;
import com.seckill.common.api.goods.vo.GoodsVo;
import com.seckill.miaosha.dao.GoodsDao;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Service(interfaceClass = GoodsServiceApi.class,version = "execution")
public class GoodsService implements GoodsServiceApi {
    @Autowired
    GoodsDao goodsDao;
    @Override
    public List<GoodsVo> listGoodsVo() {
        return goodsDao.listGoodsVo();
    }

    @Override
    public GoodsVo getGoodsVoById(long goodId) {
        return goodsDao.getGoodsVoById(goodId);
    }

    @Override
    public int getStockByGoodsId(long goodsId) {
        return goodsDao.getStockByGoodsId(goodsId);
    }

    @Override
    public Boolean redurceStock(GoodsVo goods) {
        long miaoshaGoodsId = goods.getId();
        int res = goodsDao.redurceStock(miaoshaGoodsId);
        return res > 0;
    }
}
