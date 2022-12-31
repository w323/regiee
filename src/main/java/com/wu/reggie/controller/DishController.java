package com.wu.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.exc.IgnoredPropertyException;
import com.wu.reggie.common.R;
import com.wu.reggie.dto.DishDto;
import com.wu.reggie.entity.Category;
import com.wu.reggie.entity.Dish;
import com.wu.reggie.entity.DishFlavor;
import com.wu.reggie.service.CategoryService;
import com.wu.reggie.service.DishFlavorService;
import com.wu.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.PagesPerMinute;
import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {

        log.info("添加菜单：{}", dishDto.toString());

        dishService.savaWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /**
     * 显示菜品信息
     * @param page 第几页
     * @param pageSize 每页多少条数据
     * @param name 根据名字查询数据
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        //构造分页对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);

        //解决前端显示的菜品分类
        Page<DishDto> dishDtoPage = new Page<>();


        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //过滤条件
        queryWrapper.like(name != null, Dish::getName, name);
        //排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo, queryWrapper);

        //对象拷贝 ,忽略不拷贝的属性
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);

    }

    @GetMapping("/{id}")//rustful风格
    public R<DishDto> get(@PathVariable Long id) {

        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {

        log.info("添加菜单：{}", dishDto.toString());

        dishService.updateWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }


    /**
     * 更改菜品的销售状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> change(@PathVariable int status, Long ids) {

        //停售起售还没有解决，明天再战！！！
        //已经解决了
        dishService.changeStatus(status,ids);
        return R.success("状态修改成功");
    }


    @DeleteMapping()
    public R<String> delete(Long ids) {

        //1.根据要删除的菜品id删除这个菜品
        dishService.removeById(ids);
        //2.再根据这个菜品id去删除dish_flavor里的菜品风味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        //条件构造器
        queryWrapper.eq(DishFlavor::getDishId,ids);
        dishFlavorService.remove(queryWrapper);
        return R.success("删除成功");
    }


    /**
     * 根据条件查询菜品数据
     * @param dish
     * @return
     */
   /* @GetMapping("/list")
    @Transactional
    public R<List<Dish>> list(Dish dish) {//虽然传过来的是id，但dish参数可以进行封装，具有更好的扩展性

        //1.构造查询条件对象
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null, Dish::getCategoryId,dish.getCategoryId());

        queryWrapper.eq(Dish::getStatus,1);//只查询在售的菜品信息
        //2.添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        return R.success(list);

    }*/

    /**
     * 根据条件查询菜品数据,显示给前端面，展示菜品的口味信息，手机端和网页端都可以兼容
     * @param dish
     * @return
     */
    @GetMapping("/list")
    @Transactional
    public R<List<DishDto>> list(Dish dish) {//虽然传过来的是id，但dish参数可以进行封装，具有更好的扩展性

        //1.构造查询条件对象
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null, Dish::getCategoryId,dish.getCategoryId());

        queryWrapper.eq(Dish::getStatus,1);//只查询在售的菜品信息
        //2.添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品id
            Long dishId = item.getId();

            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> flavorList = dishFlavorService.list(queryWrapper1);

            dishDto.setFlavors(flavorList);

            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);

    }

}
