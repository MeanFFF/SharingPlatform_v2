package com.platform.common;

import com.google.common.collect.Sets;

import java.util.Set;

public class Const {
    public static final String CURRENT_USER = "currentUser";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    public interface Role{
        int ROLE_CUSTOMER = 1; //普通用户
        int ROLE_ADMIN = 0;//管理员
    }

    public interface FileListOrderBy{
        Set<String> FILE_ORDERBY = Sets.newHashSet("times-desc", "create_time-desc");
    }

    public interface UserStatus{
        int NOT_ACTIVE = 0;//未激活
        int NORMAL = 1;//正常
        int DELETE = 2;//封号
    }

    public enum ReviewStatusEnum{
        HIDE(0,false),//显示
        SHOW(1,true);//隐藏

        ReviewStatusEnum(int code,boolean value){
            this.code = code;
            this.value = value;
        }

        private boolean value;
        private int code;

        public boolean getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static ReviewStatusEnum codeOf(int code){
            for(ReviewStatusEnum reviewStatusEnum : values()){
                if(reviewStatusEnum.getCode() == code){
                    return reviewStatusEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }

    }

    public interface SearchUserTag{
        int NORMAL = 0;//平常
        int ORDER_BY_SOCRE = 1;//按照积分数排序
        int ORDER_BY_RESUME_SOCRE = 2;//按照消耗的积分数排序
    }

    public interface SearchFileTag{
        int NORMAL = 0;
        int GET_UNCHECKED = 1;
        int ORDER_BY_TIMES = 2;
    }

    public enum ProductStatusEnum{
        ON_LINE(0, "在售"),
        OFF_LINE(1, "下架"),
        DELETE(2, "删除");

        ProductStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }

        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static ProductStatusEnum codeOf(int code){
            for(ProductStatusEnum productStatusEnum : values()){
                if(productStatusEnum.getCode() == code){
                    return productStatusEnum;
                }
            }
            return null;
        }

    }

    public enum FileStatusEnum{
        UNCHECKED(0,"未审核"),
        CHECKED(1,"已审核"),
        FAILED_CHECKED(2,"审核未通过"),
        DELETE(3,"删除");

        FileStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }

        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static FileStatusEnum codeOf(int code){
            for(FileStatusEnum fileStatusEnum : values()){
                if(fileStatusEnum.getCode() == code){
                    return fileStatusEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }

    public enum SignInScoreEnum{
        FIRST_SIGN_IN(-1, 200,"第一次签到"),
        DAY_1(1, 10, "第一天"),
        DAY_2(2, 15,"第二天"),
        DAY_3(3, 20, "第三天"),
        DAY_4(4, 25,"第四天"),
        DAY_5(5, 30, "第五天"),
        DAY_6(6, 35,"第六天"),
        DAY_7(7, 50, "第七天");


        SignInScoreEnum(int code,int value, String desc){
            this.code = code;
            this.desc = desc;
            this.value = value;
        }

        private int value;
        private String desc;
        private int code;

        public int getValue() {
            return value;
        }

        public String getDesc() {
            return desc;
        }

        public int getCode() {
            return code;
        }

        public static SignInScoreEnum codeOf(int code){
            for(SignInScoreEnum signInScoreEnum : values()){
                if(signInScoreEnum.getCode() == code){
                    return signInScoreEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }

    }


    public enum ProductCategoryEnum{
        SCORE(1, "积分类"),
        VIP(2, "会员类"),
        OTHER(3, "其他类");

        ProductCategoryEnum(int code,String value){
            this.code = code;
            this.value = value;
        }

        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static ProductCategoryEnum codeOf(int code){
            for(ProductCategoryEnum productCategoryEnum : values()){
                if(productCategoryEnum.getCode() == code){
                    return productCategoryEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }

    }

    public enum PaymentTypeEnum{
        ONLINE_PAY(1,"在线支付");

        PaymentTypeEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }


        public static PaymentTypeEnum codeOf(int code){
            for(PaymentTypeEnum paymentTypeEnum : values()){
                if(paymentTypeEnum.getCode() == code){
                    return paymentTypeEnum;
                }
            }
            throw new RuntimeException("么有找到对应的枚举");
        }

    }

    public enum OrderStatusEnum{
        CANCELED(0,"已取消"),
        NO_PAY(10,"未支付"),
        PAID(20,"已付款, 交易成功");


        OrderStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static OrderStatusEnum codeOf(int code){
            for(OrderStatusEnum orderStatusEnum : values()){
                if(orderStatusEnum.getCode() == code){
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }

    public interface  AlipayCallback{
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }

    public enum PayPlatformEnum{
        ALIPAY(1,"支付宝");

        PayPlatformEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }



}
