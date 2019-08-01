package com.platform.vo;
import java.util.Date;
import java.util.List;

public class FileDetailVo {
    private Integer id;

    private String name;

    private Integer status;

    private Integer categoryId;

    private String categoryDesc;

    private Integer parentCategoryId;

    private String parentCategoryDesc;

    private Integer uploadUserId;

    private String uploadUsername;

    private String detail;

    private Integer score;

    private Integer times;

    private Date createTime;

    private Date updateTime;

    private List<ReviewVo> reviewVos;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getCategoryDesc() {
        return categoryDesc;
    }

    public void setCategoryDesc(String categoryDesc) {
        this.categoryDesc = categoryDesc;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getUploadUsername() {
        return uploadUsername;
    }

    public void setUploadUsername(String uploadUsername) {
        this.uploadUsername = uploadUsername;
    }


    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
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

    public List<ReviewVo> getReviewVos() {
        return reviewVos;
    }

    public void setReviewVos(List<ReviewVo> reviewVos) {
        this.reviewVos = reviewVos;
    }

    public Integer getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Integer parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public String getParentCategoryDesc() {
        return parentCategoryDesc;
    }

    public void setParentCategoryDesc(String parentCategoryDesc) {
        this.parentCategoryDesc = parentCategoryDesc;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
