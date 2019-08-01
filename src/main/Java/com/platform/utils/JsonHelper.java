package com.platform.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JsonHelper {


    /**
     * Java对象序列化为JSON字符串
     *
     * @param obj Java对象
     * @return json字符串
     */
    public static String toJson(Object obj) {
        return JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue);
    }
}