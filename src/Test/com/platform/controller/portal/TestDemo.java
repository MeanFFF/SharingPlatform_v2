package com.platform.controller.portal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TestDemo {

    private static boolean isYesterday(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String param = sdf.format(date);
        String now = sdf.format(new Date().getTime() - 86400000L);
        return param.equals(now);
    }

    @Test
    public void test() {
        Date date = new Date(new Date().getTime() - 86400000L);
        System.out.println(date);

    }




//    @Test
//    public void Ttt() {
//        String address = "ftp://172.26.93.207/be9325b8-1f8b-4603-9137-19939e635302.exe";
//        System.out.println(address.substring(address.lastIndexOf("/") + 1));
//        int value = 7 % 7 + 1;
//        System.out.println(value);
//        String email = "3062168783@qq.com";
//
//        System.out.println(email.toUpperCase());

//        String str = "100积分套餐";
//
//        System.out.println(str.substring(str.lastIndexOf("积")));
//        String str = "1000积分";
//        int score = Integer.parseInt(str.substring(0, str.indexOf("积分")));
//        System.out.println(score);
//        // 1. 生成一个Jedis对象, 这个对象负责和指定Reids节点进行通信
//        Jedis jedis = new Jedis("127.0.0.1", 6379);
//        // 2. jedis执行set操作
//        jedis.set("hello", "world");
//        // 3. jedis执行get操作, value="world"
//        String value = jedis.get("hello");
//        System.out.println(value);

        // 初始化Jedis连接池, 通常来讲JedisPool是单例的
//        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
//        JedisPool jedisPool = new JedisPool(poolConfig, "127.0.0.1", 6379);
//
//        Jedis jedis = null;
//        try {
//            // 1. 从连接池获取jedis对象
//            jedis = jedisPool.getResource();
//            // 2. 执行操作
//            jedis.set("hello", "world");
//        }catch (Exception e){
//            e.printStackTrace();
//        }finally {
//            if(jedis != null){
//                // 如果使用JedisPool, close操作不是关闭连接, 代表归还连接池
//            }
//        }


//    }

//    public VideoInfo get (long id){
//        String redisKey = redisPrefix + id;
//        VideoInfo videoInfo = redis.get(redisKey);
//        if(videoInfo == null){
//            videoInfo = mysql.get(id);
//            if(videoInfo != null){
//                //序列化
//                redis.set(redisKey, serializer(videoInfo));
//            }
//        }
//        return videoInfo;
//    }

//    public VideoInfo get (long id){
//        String redisKey = redisPrefix + id;
//        Map<String, String> hashMap = redis.hgetAll(redisKey);
//        VideoInfo videoInfo = transferMapToVideo(hashMap);
//        if(videoInfo == null){
//            videoInfo = mysql.get(id);
//            if(videoInfo != null){
//                redis.hmset(redisKey, transferVideoToMap(videoInfo));
//            }
//        }
//        return videoInfo;
//    }

}
