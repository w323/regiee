package com.wu.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wu.reggie.common.CustomException;
import com.wu.reggie.common.R;
import com.wu.reggie.dto.DishDto;
import com.wu.reggie.dto.SetmealDto;
import com.wu.reggie.entity.Dish;
import com.wu.reggie.entity.DishFlavor;
import com.wu.reggie.entity.Setmeal;
import com.wu.reggie.entity.SetmealDish;
import com.wu.reggie.mapper.SetmealMapper;
import com.wu.reggie.service.SetmealDishService;
import com.wu.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import javax.print.PrintService;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {


    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    public void saveWithDish(SetmealDto setmealDto) {

        //1.保存到惨的基本信息，操作setmeal表，执行insert操作
        //这里可以直接使用setmealdto作为参数，是因为setmealdto是setmeal的子类，在进行封装时，能封装上
        List<SetmealDish> setmealDishes1 = setmealDto.getSetmealDishes();
        BigDecimal count = BigDecimal.valueOf(0);
        for (int i = 0; i < setmealDishes1.size(); i++) {
            BigDecimal price = setmealDishes1.get(i).getPrice();
            Integer copies = setmealDishes1.get(i).getCopies();
            BigDecimal copy = BigDecimal.valueOf(copies);
            count =  count.add(price.multiply(copy));
            /*
            这里对价格进行纠正，避免前端传过来的价格有问题，进行校验
             */
        }
        setmealDto.setPrice(count);
        this.save(setmealDto);


        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
       }).collect(Collectors.toList());
        //2.保存套餐和菜品的关联关系，操作setmeal_dish,执行insert操作
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐，同时删除套餐管理的菜品数据
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {

        //select count(*) from setmeal where id in (ids) and status = 1;
        //1.查询套餐状态，看是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(queryWrapper);
        if(count > 0) {
            //4.如果不能删除，抛出一个业务异常
            throw new CustomException("有起售的菜品，不能删除");
        }
        //2.如果可以删除，先删除套餐表中的数据
        this.removeByIds(ids);
        //3.删除关系表中的数据
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(wrapper);

    }

    /**
     * 改变套餐的售卖状态
     * @param status
     * @param id
     */
    @Override
    public void changeStatus(int status, Long id) {
        Setmeal setmeal = this.getById(id);
        setmeal.setStatus(status);
        this.updateById(setmeal);
    }

    @Override
    public SetmealDto getByIdWithSetmeal(Long id) {

        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId,setmeal.getId());

        List<SetmealDish> list = setmealDishService.list(wrapper);

        setmealDto.setSetmealDishes(list);

        return setmealDto;
    }

    /**
     * 修改套餐
     * @param setmealDto
     */
    @Override
    public void updateWithSetmeal(SetmealDto setmealDto) {
        this.updateById(setmealDto);

        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(wrapper);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes = setmealDishes.stream().map((item) ->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        BigDecimal count = BigDecimal.valueOf(0);
        for (int i = 0; i < setmealDishes.size(); i++) {
            BigDecimal price = setmealDishes.get(i).getPrice();
            Integer copies = setmealDishes.get(i).getCopies();
            BigDecimal copy = BigDecimal.valueOf(copies);
            count =  count.add(price.multiply(copy));
            /*
            这里对价格进行纠正，避免前端传过来的价格有问题，进行校验
             */
        }
        setmealDto.setPrice(count);
        //更新套餐的价格？？？？？？
        this.updateById(setmealDto);

        setmealDishService.saveBatch(setmealDishes);


    }

}
