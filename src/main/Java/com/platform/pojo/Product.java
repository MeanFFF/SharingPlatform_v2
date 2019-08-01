package com.platform.pojo;

import java.math.BigDecimal;
import java.util.Date;

public class Product {
    private Integer id;

    private Integer category;

    private String name;

    private BigDecimal price;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    private String detail;

    public Product(Integer id, Integer category, String name, BigDecimal price, Integer status, Date createTime, Date updateTime, String detail) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.price = price;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.detail = detail;
    }

    public Product(Integer id, Integer category, String name, BigDecimal price, Integer status, Date createTime, Date updateTime) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.price = price;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Product(Integer id, Integer category, String name, BigDecimal price, String detail) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.price = price;
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", category=" + category +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", detail='" + detail + '\'' +
                '}';
    }

    public Product() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail == null ? null : detail.trim();
    }
}