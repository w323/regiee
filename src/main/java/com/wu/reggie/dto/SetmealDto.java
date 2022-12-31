package com.wu.reggie.dto;


import com.wu.reggie.entity.Setmeal;
import com.wu.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
