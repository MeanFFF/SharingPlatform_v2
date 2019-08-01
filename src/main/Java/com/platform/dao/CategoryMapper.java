package com.platform.dao;

import com.platform.pojo.Category;

import java.util.List;

public interface CategoryMapper {
    int insert(Category record);

    int insertSelective(Category record);

    List<Category> selectCategoryChildrenByParentId(Integer parentId);

    int updateByPrimaryKeySelective(Category category);

    Category selectByPrimaryKey(Integer categoryId);
}