package com.wu.reggie;

import com.wu.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @version 1.0
 * @Author 吴俊彪
 * @Date 2022/10/15/015
 */
@Slf4j
@SpringBootApplication
@ServletComponentScan //扫描servlet组件的配置 比如过滤器
@EnableTransactionManagement//开启事务注解的支持
public class ReggieApplication {
    public static void main(String[] args) {

        ConfigurableApplicationContext run = SpringApplication.run(ReggieApplication.class);
        log.info("服务已启动");
        /*
        Map<String, Object> beansWithAnnotation = run.getBeansWithAnnotation(RestController.class);
        for(Map.Entry<String,Object> bean : beansWithAnnotation.entrySet()) {
            System.out.println(bean);
        }*/
    }
}
