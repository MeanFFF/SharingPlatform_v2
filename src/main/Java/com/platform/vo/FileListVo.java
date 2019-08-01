package com.platform.vo;


import java.util.Date;

public class FileListVo {
    private Integer id;

    private String name;

    private Integer score;

    private Integer times;

    private Integer status;

    private Integer reviewCounts;

    private String statusDesc;

    private Date createTime;

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }
    public Integer getReviewCounts() {
        return reviewCounts;
    }

    public void setReviewCounts(Integer reviewCounts) {
        this.reviewCounts = reviewCounts;
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
        this.name = name;
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
}
