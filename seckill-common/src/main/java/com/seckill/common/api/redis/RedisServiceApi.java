package com.seckill.common.api.redis;


import com.seckill.common.rediskeyconf.KeyPrefix;

public interface RedisServiceApi {

    public Boolean exists(KeyPrefix keyPrefix, String key);

    /*
     * 原子增加
     * @param keyPrefix
     * @param key
     * @return
     */
    public long increament(KeyPrefix keyPrefix, String key);

    /**
     * 原子减少
     *
     * @param keyPrefix
     * @param key
     * @return
     */
    public long decrease(KeyPrefix keyPrefix, String key);

    /**
     * 获取值
     *
     * @param keyPrefix
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(KeyPrefix keyPrefix, String key, Class<T> clazz);

    /**
     * 设置值
     *
     * @param keyPrefix
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> Boolean set(KeyPrefix keyPrefix, String key, T value);

    public Boolean delete(KeyPrefix keyPrefix, String key);

    public <T> T stringToBean(String str, Class<T> clazz);


    public <T> String beanToString(T value);
}
