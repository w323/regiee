package com.wu.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wu.reggie.common.CustomException;
import com.wu.reggie.entity.Category;
import com.wu.reggie.entity.Dish;
import com.wu.reggie.entity.Setmeal;
import com.wu.reggie.mapper.CategoryMapper;
import com.wu.reggie.service.CategoryService;
import com.wu.reggie.service.DishService;
import com.wu.reggie.service.EmployeeService;
import com.wu.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service //先继承mp的类，在实现自己的服务层接口
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id来删除分类，删除前，要看是否和其他的菜品有关联
     * @param id
     */
    @Override
    public void remove(long id) {
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        //根据id查询
        dishQueryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(dishQueryWrapper);
        //查询当前分类是否关联了其他菜品，如果关联破除异常

        if(count > 0) {
            //已经关联了菜品，抛出一个异常
            throw new CustomException("当前分类关联了菜品，不能删除");
        }
        //查询当前分类是否关联了其他套餐，如果关联破除异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count1 = setmealService.count(setmealLambdaQueryWrapper);
        if(count1 > 0) {
            //说明有关联，抛出异常
            throw new CustomException("当前分类关联了套餐，不能删除");
        }
        //正常删除分类
        super.removeById(id);

    }





}
