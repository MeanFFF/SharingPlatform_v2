package com.platform.pojo;

import java.util.Date;

public class SignIn {
    private Integer id;

    private Integer userId;

    private Date time;

    private Integer days;

    public SignIn(Integer id, Integer userId, Date time, Integer days) {
        this.id = id;
        this.userId = userId;
        this.time = time;
        this.days = days;
    }

    public SignIn() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }
}