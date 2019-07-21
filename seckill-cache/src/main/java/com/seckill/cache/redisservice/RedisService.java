package com.seckill.cache.redisservice;

import com.alibaba.fastjson.JSON;
import com.seckill.common.api.redis.RedisServiceApi;
import com.seckill.common.rediskeyconf.KeyPrefix;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
@Service(interfaceClass = RedisServiceApi.class)
public class RedisService implements RedisServiceApi{
    @Autowired
    private JedisPool jedisPool;

    /**
     * 判断是否存在
     *
     * @param keyPrefix
     * @param key
     * @return
     */
    public Boolean exists(KeyPrefix keyPrefix, String key) {
        Jedis jedis = null;
        if (key == null) {
            return false;
        }
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            Boolean ex = jedis.exists(realKey);
            return ex;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /*
     * 原子增加
     * @param keyPrefix
     * @param key
     * @return
     */
    public long increament(KeyPrefix keyPrefix, String key) {
        Jedis jedis = null;
        if (key == null) {
            return -1;
        }
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            Long res = jedis.incr(realKey);
            return res;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 原子减少
     *
     * @param keyPrefix
     * @param key
     * @return
     */
    public long decrease(KeyPrefix keyPrefix, String key) {
        Jedis jedis = null;
        if (key == null) {
            return -1;
        }
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            Long res = jedis.decr(realKey);
            return res;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


    /**
     * 获取值
     *
     * @param keyPrefix
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(KeyPrefix keyPrefix, String key, Class<T> clazz) {
        Jedis jedis = null;
        if (key == null) {
            return null;
        }
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            String str = jedis.get(realKey);
            T t = stringToBean(str, clazz);
            return t;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 设置值
     *
     * @param keyPrefix
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> Boolean set(KeyPrefix keyPrefix, String key, T value) {
        Jedis jedis = null;
        if (key == null) {
            return false;
        }
        try {
            jedis = jedisPool.getResource();
            String str = beanToString(value);
            if (str == null || str.length() <= 0) {
                return false;
            }
            String realKey = keyPrefix.getPrefix() + key;
            int expireTime = keyPrefix.getExpireTime();
            if (expireTime <= 0) {
                jedis.set(realKey, str);
            } else {
                jedis.setex(realKey, expireTime, str);
            }
            return true;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Boolean delete(KeyPrefix keyPrefix, String key) {
        Jedis jedis = null;
        if (key == null) {
            return false;
        }
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            Long res = jedis.del(realKey);
            return res > 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


    public <T> T stringToBean(String str, Class<T> clazz) {
        if (str == null || str.length() <= 0 || clazz == null) {
            return null;
        }
        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(str);
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(str);
        } else if (clazz == String.class) {
            return (T) str;
        } else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }
    }


    public <T> String beanToString(T value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class) {
            return "" + value;
        } else if (clazz == long.class || clazz == Long.class) {
            return "" + value;
        } else if (clazz == String.class) {
            return (String) value;
        } else {
            return JSON.toJSONString(value);
        }
    }

}
