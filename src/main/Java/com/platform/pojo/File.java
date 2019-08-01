package com.platform.pojo;

import java.util.Date;

public class File {
    private Integer id;

    private String name;

    private Integer categoryId;

    private Integer uploadUserId;

    private String address;

    private String detail;

    private Integer score;

    private Integer times;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    public File(Integer id, String name, Integer categoryId, Integer uploadUserId, String address, String detail, Integer score, Integer times, Integer status, Date createTime, Date updateTime) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.uploadUserId = uploadUserId;
        this.address = address;
        this.detail = detail;
        this.score = score;
        this.times = times;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public File() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getUploadUserId() {
        return uploadUserId;
    }

    public void setUploadUserId(Integer uploadUserId) {
        this.uploadUserId = uploadUserId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail == null ? null : detail.trim();
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}