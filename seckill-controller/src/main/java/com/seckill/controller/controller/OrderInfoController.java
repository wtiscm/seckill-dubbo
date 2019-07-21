package com.seckill.controller.controller;

import com.seckill.common.api.goods.GoodsServiceApi;
import com.seckill.common.api.goods.vo.GoodsVo;
import com.seckill.common.api.order.OrderServiceApi;
import com.seckill.common.api.order.vo.OrderDetailVo;
import com.seckill.common.api.seckill.domain.OrderInfo;
import com.seckill.common.api.user.vo.MiaoshaUser;
import com.seckill.controller.result.Result;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/order")
public class OrderInfoController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Reference(interfaceClass = OrderServiceApi.class)
    OrderServiceApi orderService;
    @Reference(interfaceClass = GoodsServiceApi.class,version = "goods")
    GoodsServiceApi goodsService;

    // 订单详情
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public Result<OrderDetailVo> orderDetail(MiaoshaUser miaoshaUser, @RequestParam("orderId") long orderId) {
        OrderInfo orderInfo = orderService.getOrderByorderId(orderId);
        GoodsVo goodsVo = goodsService.getGoodsVoById(orderInfo.getGoodsId());
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setGoodsVo(goodsVo);
        orderDetailVo.setOrder(orderInfo);
        return Result.success(orderDetailVo);
    }

}
