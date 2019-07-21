package com.seckill.user.userservice;

import com.seckill.common.api.redis.RedisServiceApi;
import com.seckill.common.api.user.UserServiceApi;
import com.seckill.common.api.user.vo.LoginVo;
import com.seckill.common.api.user.vo.MiaoshaUser;
import com.seckill.common.exception.GlobalException;
import com.seckill.common.rediskeyconf.SeckillUserKey;
import com.seckill.common.result.CodeMsg;
import com.seckill.common.util.MD5util;
import com.seckill.common.util.UUIDutil;
import com.seckill.user.dao.MiaoshaUserDao;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Service(interfaceClass = UserServiceApi.class)
public class UserService implements UserServiceApi {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Reference(interfaceClass = RedisServiceApi.class)
    RedisServiceApi redisService;
    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Override
    public String doLogin(LoginVo loginVo) {
        String mobile = loginVo.getMobile();
        String passWord = loginVo.getPassWord();
        MiaoshaUser miaoshaUser = getById(Long.parseLong(mobile));
        if (miaoshaUser == null) {
            throw new GlobalException(CodeMsg.USER_NOTEXISTS);
        }
        String salt = miaoshaUser.getSalt();
        String passWordDb = miaoshaUser.getPassword();
        String execPass = MD5util.formPassToDbPass(passWord, salt);
        if (!execPass.equals(passWordDb)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        String tokenNum = UUIDutil.getUUID();
        redisService.set(SeckillUserKey.getByToken, tokenNum, miaoshaUser);
        return tokenNum;
    }

    @Override
    public MiaoshaUser getById(long id) {
        // redis取缓存
        MiaoshaUser user = redisService.get(SeckillUserKey.getById, "" + id, MiaoshaUser.class);
        if (user == null) {
            logger.warn("every time redis insert??????????");
            // 缓存中不存在,去数据库读
            user = miaoshaUserDao.getById(id);
            // 用户未注册,返回null
            // TODO 有未注册用户反复登陆，穿透的问题
            if (user == null){
                return null;
            }
            // 用户注册了,写入redis
            redisService.set(SeckillUserKey.getById, "" + id, user);
            return user;
        }
        return user;
    }

    @Override
    public Boolean updatePassword(long id, String password, String token) {
        MiaoshaUser user = getById(id);
        if (user == null) {
            throw new GlobalException(CodeMsg.USER_NOTEXISTS);
        }
        MiaoshaUser updateUser = new MiaoshaUser();
        updateUser.setId(id);
        updateUser.setPassword(MD5util.formPassToDbPass(password, user.getSalt()));
        // 写入数据库
        //TODO
        miaoshaUserDao.updatePassword(updateUser);
        // 删除redis中用户信息缓存，更新token信息缓存
        redisService.delete(SeckillUserKey.getById, "" + id);
        user.setPassword(updateUser.getPassword());
        redisService.set(SeckillUserKey.getByToken, token, user);
        return true;
    }

    @Override
    public MiaoshaUser getByToken(String token) {
        if (token == null) {
            return null;
        }
        MiaoshaUser miaoshaUser = redisService.get(SeckillUserKey.getByToken, token, MiaoshaUser.class);
        return miaoshaUser;
    }

    @Override
    public MiaoshaUser getAndUpdateByToken(String token) {
        if (token == null) {
            return null;
        }
        MiaoshaUser miaoshaUser = redisService.get(SeckillUserKey.getByToken, token, MiaoshaUser.class);
        return miaoshaUser;
    }
}
