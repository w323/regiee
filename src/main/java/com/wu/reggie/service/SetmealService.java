package com.wu.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wu.reggie.common.R;
import com.wu.reggie.dto.SetmealDto;
import com.wu.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时删除套餐管理的菜品数据
     * @param ids
     */
    void removeWithDish(List<Long> ids);

    void changeStatus(int status, Long id);

    SetmealDto getByIdWithSetmeal(Long id);

    void updateWithSetmeal(SetmealDto setmealDto);
}
