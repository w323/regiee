package com.wu.reggie.config;


import com.wu.reggie.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.swing.*;
import java.util.List;

/**
 * @version 1.0
 * @Author 吴俊彪
 * @Date 2022/10/15/015
 */
@Slf4j
@Configuration
public class WebMVCConfig extends WebMvcConfigurationSupport {

    /**
     * 设置静态资源映射，springboot本来的默认静态资源是放在static 、 temple文件夹下的，
     * 如果要改变，就自己改变一下路径
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("静态资源映射已开启~~~");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    /**
     * 扩展mvc框架的消息转化器
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("消息转换器.....");
        //创建消息转换器
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();

        //设置对象转换器，底层使用jackson将java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将转换器对象追加到消息转换器的容器中
        converters.add(0,messageConverter);//加在第一个默认最先使用
    }
}
