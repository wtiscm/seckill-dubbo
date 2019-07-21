package com.seckill.controller.controller;

import com.seckill.common.api.goods.GoodsServiceApi;
import com.seckill.common.api.goods.vo.GoodsDetailVo;
import com.seckill.common.api.goods.vo.GoodsVo;
import com.seckill.common.api.redis.RedisServiceApi;
import com.seckill.common.api.user.UserServiceApi;
import com.seckill.common.api.user.vo.MiaoshaUser;
import com.seckill.common.exception.GlobalException;
import com.seckill.common.rediskeyconf.GoodsKey;
import com.seckill.common.result.CodeMsg;
import com.seckill.controller.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Reference(interfaceClass = UserServiceApi.class)
    UserServiceApi userService;
    @Reference(interfaceClass = RedisServiceApi.class)
    RedisServiceApi redisService;
    @Reference(interfaceClass = GoodsServiceApi.class, version = "goods")
    GoodsServiceApi goodsService;
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @RequestMapping(value = "/to_list", produces = "text/html")
    @ResponseBody
    public String list(HttpServletRequest request, HttpServletResponse response,
                       Model model, MiaoshaUser miaoshaUser) throws IOException {
        if (miaoshaUser == null) {
            response.sendRedirect("/seckill/to_login");
            return "";
        }
        // 从redis中取商品列表页
        String html = redisService.get(GoodsKey.goodsList, "", String.class);
        // redis中没有，自己渲染，并放入rdis中
        if (StringUtils.isEmpty(html)) {
            model.addAttribute("user", miaoshaUser);
            List<GoodsVo> goodsList = goodsService.listGoodsVo();
            model.addAttribute("goodsList", goodsList);
            WebContext context = new WebContext(request, response, request.getServletContext(),
                    request.getLocale(), model.asMap());
            html = thymeleafViewResolver.getTemplateEngine().process("goodsList", context);
            if (StringUtils.isEmpty(html)) {
                throw new GlobalException(CodeMsg.INNERERROR);
            }
            redisService.set(GoodsKey.goodsList, "", html);
            logger.info(">>>>redis insert");
            return html;
        }
        return html;
    }


    @RequestMapping(value = "/to_detail/{goodsId}", produces = "text/html")
    @ResponseBody
    public String detail(HttpServletRequest request, HttpServletResponse response,
                         Model model, MiaoshaUser miaoshaUser, @RequestParam("goodsId") long goodsId) throws IOException {
        if (miaoshaUser == null) {
            response.sendRedirect("/seckill/to_login");
            return "login";
        }
        // 从redis中取商品详情
        String html = redisService.get(GoodsKey.goodsDetail, "" + goodsId, String.class);
        // redis中没有，自己渲染，并放入redis中
        if (StringUtils.isEmpty(html)) {
            model.addAttribute("user", miaoshaUser);
            GoodsVo good = goodsService.getGoodsVoById(goodsId);
            model.addAttribute("goods", good);
            long startDate = good.getStartDate().getTime();
            long endDate = good.getEndDate().getTime();
            long now = System.currentTimeMillis();
            int miaoshaStatus = 0;
            int remainSeconds = 0;
            if (now < startDate) {
                // 秒杀未开始，倒计时
                miaoshaStatus = 0;
                remainSeconds = (int) ((startDate - now) / 1000);
            } else if (now > endDate) {
                // 秒杀结束
                miaoshaStatus = 2;
                remainSeconds = -1;
            } else {
                // 秒杀进行中
                miaoshaStatus = 1;
                remainSeconds = 0;
            }
            model.addAttribute("miaoshaStatus", miaoshaStatus);
            model.addAttribute("remainSeconds", remainSeconds);
            WebContext context = new WebContext(request, response, request.getServletContext(),
                    request.getLocale(), model.asMap());
            html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", context);
            if (StringUtils.isEmpty(html)) {
                throw new GlobalException(CodeMsg.INNERERROR);
            }
            redisService.set(GoodsKey.goodsDetail, "" + goodsId, html);
            logger.info(">>>>redis insert");
            return html;
        }
        logger.info(">>>>direct read");
        return html;
    }

    @RequestMapping(value = "/detail/{goodsId}", method = RequestMethod.GET)
    @ResponseBody
    public Result<GoodsDetailVo> goodsDetail(HttpServletRequest request, HttpServletResponse response,
                                             Model model, MiaoshaUser miaoshaUser, @PathVariable("goodsId") long goodsId) throws IOException {
        GoodsVo goods = goodsService.getGoodsVoById(goodsId);
        long startDate = goods.getStartDate().getTime();
        long endDate = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int miaoshaStatus = 0;
        int remainSeconds = 0;
        if (now < startDate) {
            // 秒杀未开始
            miaoshaStatus = 0;
            remainSeconds = (int) ((startDate - now) / 1000);
        } else if (now > endDate) {
            // 秒杀结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else {
            // 秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        goodsDetailVo.setGoodsVo(goods);
        goodsDetailVo.setMiaoshaUser(miaoshaUser);
        goodsDetailVo.setMiaoshaStatus(miaoshaStatus);
        goodsDetailVo.setRemainSeconds(remainSeconds);
        return Result.success(goodsDetailVo);
    }

}
