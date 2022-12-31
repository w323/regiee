package com.wu.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wu.reggie.common.R;
import com.wu.reggie.entity.Employee;
import com.wu.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.aspectj.InstantiationModelAwarePointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@RestController //返回的不是一个页面，就用这个，返回的形式是随你返回类型去包装的
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {//前端返回的数据如果是json类型，使用@requestbody 注解，将其包装为一个类

        //1.将页面提交的密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、将页面提交的用户名进行数据库查询
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        //上面两步是将wrapper封装好，然后直接去调用service层的方法
        Employee emp = employeeService.getOne(queryWrapper);//由于在数据库中，用户名是设置为唯一的

        //3.看是否查到这个用户名,为空，返回
        if (emp == null) {
            return R.error("登陆失败");
        }

        //4.不为空，则比对密码
        if (!emp.getPassword().equals(password)) {
            return R.error("登陆失败");
        }

        //5.说明登陆成功，查看用户状态,为0表示账号已禁用
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }

        //6.登陆成功，将员工id存入session，并返回登陆成功结果
        request.getSession().setAttribute("employee", emp.getId());
        Object employee1 = request.getSession().getAttribute("employee");
        log.info("获得session是：{}",employee1);
        return R.success(emp);
    }

    //退出
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {

        request.getSession().removeAttribute("employee");

        return R.success("退出成功");
    }

    //新增员工
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {//前段时json格式，有这个注解进行封装
        log.info("新增员工，员工信息：{}",employee.toString());

        //设置初始密码123456，需要进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        //获得当前登录用户的id,先在已经解决了
//        Long empId = (Long) request.getSession().getAttribute("employee");
        /*
        这里有点小问题，没有解决，获取不到设置的session，但是数据库中这些字段设置为非空的，
        所以只有改变数据库中的字段，才能解决这个问题了
         */

//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    /**
     * 员工分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize, String name) {
        log.info("page = {},size = {}, name = {}",page,pageSize,name);

        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        wrapper.like(name != null,Employee::getName,name);

        //添加排序条件
        wrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo, wrapper);//在mp内部已经构造好了
        return R.success(pageInfo);
    }


    @PutMapping
    public R<String> update(@RequestBody Employee employee, HttpServletRequest request) {
        log.info(employee.toString());

        long id = Thread.currentThread().getId();
        log.info("update线程id {}",id);

        //employee.setUpdateTime(LocalDateTime.now());
       // Long empId = (Long) request.getSession().getAttribute("employee");
       // employee.setUpdateUser(empId);

        /**
         * 这里有一个更新不成功的问题
         * 原因是：前端显示的id出现了精度问题，是由js导致的，只能保证16位的精度
         * 导致根据id去更新操作失败
         * 解决方法：把id保存为字符串的形式
         */
        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }

    /**
     * 修改员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable("id") long id) {
        log.info("根据id查询对象。。。。");
        Employee emp = employeeService.getById(id);
        if(emp != null) {
            return R.success(emp);
        }
        return R.error("没有查询到员工");
    }
}
