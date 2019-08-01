package com.platform.utils;

import com.google.common.collect.Maps;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.*;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Map;

public class EntityUtils {

    static {
        ConvertUtils.register(new LongConverter(null), Long.class);
        ConvertUtils.register(new ByteConverter(null), Byte.class);
        ConvertUtils.register(new IntegerConverter(null), Integer.class);
        ConvertUtils.register(new DoubleConverter(null), Double.class);
        ConvertUtils.register(new ShortConverter(null), Short.class);
        ConvertUtils.register(new FloatConverter(null), Float.class);
        ConvertUtils.register(new Converter() {
            public Object convert(Class type, Object value) {
                if (value == null || value == "") {
                    return null;
                }
                return new Date(Long.valueOf((String) value));
            }
        }, Date.class);
    }

    public static Map<String, String> objectToHash(Object obj) {
        try {
            Map<String, String> map = Maps.newHashMap();
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                if (!property.getName().equals("class")) {
                    if (property.getReadMethod().invoke(obj) != null) {
                        // 时间类型会错乱所以吧时间手动转换成long;
                        if (property.getReadMethod().invoke(obj) != null) {
                            if ("java.util.Date".equals(property.getPropertyType().getTypeName())) {
                                Date invoke = (Date) property.getReadMethod().invoke(obj);
                                long time = invoke.getTime();
                                map.put(property.getName(), String.valueOf(time));
                            } else {
                                map.put(property.getName(), "" + property.getReadMethod().invoke(obj));
                            }
                        }
                    }else{
                        map.put(property.getName(), "");
                    }
                }
            }
            return map;
        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T hashToObject2(Map<?, ?> map, Class t) {
        try {
            Object o = t.newInstance();
            BeanUtils.populate(o, (Map) map);
            return (T) o;

        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
