package com.wu.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wu.reggie.dto.DishDto;
import com.wu.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dishflavor
    void savaWithFlavor(DishDto dishDto);

    //根据菜品id查询菜品信息和口味信息
    DishDto getByIdWithFlavor(Long id);

    //修改菜品信息和口味信息
    void updateWithFlavor(DishDto dishDto);

    //修改菜品的状态
    void changeStatus(int status , Long ids);
}
