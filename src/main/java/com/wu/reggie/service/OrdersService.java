package com.wu.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wu.reggie.entity.Orders;

public interface OrdersService extends IService<Orders> {

    void submit(Orders orders);

}
