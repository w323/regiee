package com.wu.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wu.reggie.dto.DishDto;
import com.wu.reggie.entity.Dish;
import com.wu.reggie.entity.DishFlavor;
import com.wu.reggie.mapper.DishMapper;
import com.wu.reggie.service.DishFlavorService;
import com.wu.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {


    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存口味数据
     * @param dishDto
     */
    @Override
    @Transactional//涉及多表，进行事务控制，为了能起作用，在项目启动类中，设置一下
    public void savaWithFlavor(DishDto dishDto) {

        //保存菜品信息
        this.save(dishDto);

        //这里为什么可以得到id呢，在保存菜品的时候就已经得到了id，所以可以得到id
        Long dishId = dishDto.getId();

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存口味数据到dish_flavor表中
        dishFlavorService.saveBatch(flavors);
    }

    //根据菜品id查询菜品信息和口味信息
    @Override
    @Transactional
    public DishDto getByIdWithFlavor(Long id) {

        //1.查询菜品信息
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //2.查询当前菜品的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());

        List<DishFlavor> flavorList = dishFlavorService.list(queryWrapper);

        //给dishDto的flavor属性赋值
        dishDto.setFlavors(flavorList);

        return dishDto;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {

        //1.更新dish表字段
        this.updateById(dishDto);
        //2.删除dish_flavor的信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //3.添加dish_flavor的信息
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 修改菜品的销售状态
     * @param status
     * @param ids
     */
    @Override
    public void changeStatus(int status, Long ids) {
        Dish changeDish = this.getById(ids);
        changeDish.setStatus(status);
        this.updateById(changeDish);
    }
}
