package com.wu.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wu.reggie.entity.Category;

public interface CategoryService extends IService<Category> {

    void remove(long id);
}
