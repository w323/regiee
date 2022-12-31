package com.wu.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wu.reggie.entity.Employee;
import com.wu.reggie.mapper.EmployeeMapper;
import com.wu.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService {

}

