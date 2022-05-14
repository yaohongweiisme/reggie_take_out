package com.wei.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wei.reggie.entity.Orders;

public interface OrderService extends IService<Orders> {

    void submit(Orders orders);
}
