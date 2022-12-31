package com.wu.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.wu.reggie.common.BaseContext;
import com.wu.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//检查用户登录，使用的servlet本身提供的
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //匹配路径的一个类
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1.获取请求的路径
        String url = request.getRequestURI();

        log.info("路径：{}", url);
        //2.设置不用过滤的路径
        String[] noFilterUrl = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/login",//移动端登录
                "/user/sendMsg"//移动端发送短信
        };
        //3.该路径是否需要进行拦截
        boolean filter = isFilter(noFilterUrl, url);
        if(filter) {
            log.info("{}不拦截",url);
            filterChain.doFilter(request,response);
            return;
        }
        //4.该路径要拦截，看是否处于登陆状态
        //4-1. 如果已经登录，放行
        if (request.getSession().getAttribute("employee") != null) {
            log.info("用户登陆，id是{}", request.getSession().getAttribute("employee"));

            long id = Thread.currentThread().getId();
            log.info("当前过滤的线程id是：{}",id);
            Long empId =(Long) request.getSession().getAttribute("employee");
            //根据是同一个线程，可以这样设置
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request, response);
            return;
        }
        //4-2. 判断移动端是否登录
        if (request.getSession().getAttribute("user") != null) {
            log.info("用户登陆，id是{}", request.getSession().getAttribute("user"));


            Long userId =(Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request, response);
            return;
        }


        //4.2. 用户未登录，要结合前端处理来写处理方式
        log.info("未登录");

        response.getWriter().write(JSON.toJSONString(R.error("未登录")));
        return;
    }

    /*
     * 这个路径是否是要拦截的路径
     * 匹配上了，返回true
     *
     * @param noFilterUrl
     * @param url
     * @return
     */

    public boolean isFilter(String[] noFilterUrl, String url) {
        for (String noUrl : noFilterUrl) {
            boolean match = PATH_MATCHER.match(noUrl, url);
            if(match ) {
                return true;
            }
        }
        return false;
    }
}
