package com.wu.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wu.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
