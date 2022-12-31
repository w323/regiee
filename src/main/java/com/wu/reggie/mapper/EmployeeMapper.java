package com.wu.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wu.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @version 1.0
 * @Author 吴俊彪
 * @Date 2022/10/15/015
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
