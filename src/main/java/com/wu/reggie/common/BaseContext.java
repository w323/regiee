package com.wu.reggie.common;

/**
 * 基于ThreadLocal封装的工具类，用于保存和获取登录用户的id
 */
public class BaseContext {

    //以线程为作用域，不会和其他线程干扰
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(long id) {
        threadLocal.set(id);
    }

    public static long getCurrentId() {
        return threadLocal.get();
    }
}
