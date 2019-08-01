package com.platform.dao.cache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class RedisDao {

//    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JedisPool jedisPool;


    public RedisDao(String ip, int port) {
        jedisPool = new JedisPool(ip, port);
    }

    public void setResetPwdUUid( String uuid, String userId){
        Jedis jedis = jedisPool.getResource();
        int timeout = 60*60;
        jedis.setex(uuid, timeout, userId);
    }

    public String getResetPwdUserId(String uuid){
        Jedis jedis = jedisPool.getResource();
        String userId = jedis.get(uuid);
        return userId;
    }

    public void remove(String key){
        Jedis jedis = jedisPool.getResource();
        jedis.del(key);
    }


}
