package com.wei.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.org.apache.xpath.internal.operations.Or;
import com.wei.reggie.common.BaseContext;
import com.wei.reggie.common.R;
import com.wei.reggie.entity.Orders;
import com.wei.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据"+orders.toString());
        orderService.submit(orders);
        return R.success("下单成功");
    }
    @GetMapping("/userPage")
    public R<Page<Orders>> userPage(int page,int pageSize){
        Page<Orders> ordersPage=new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        wrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(ordersPage,wrapper);
        return R.success(ordersPage);
    }
    @GetMapping("/page")
    public R<Page<Orders>> page(int page, int pageSize, Long number, String beginTime,String endTime){
        LocalDateTime beginTime1=null,endTime1=null;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if(beginTime!=null&&endTime!=null){
        beginTime1 = LocalDateTime.parse(beginTime,df);
        endTime1 = LocalDateTime.parse(endTime,df);}
        Page<Orders> ordersPage=new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> wrapper=new LambdaQueryWrapper<>();
        wrapper.like(number!=null,Orders::getNumber,number);
        wrapper.between(beginTime1!=null&&endTime1!=null,Orders::getOrderTime,beginTime1,endTime1);
        wrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(ordersPage,wrapper);
        return R.success(ordersPage);
    }
    @PutMapping
    public R<String> changStatus(@RequestBody Orders orders){
        LambdaUpdateWrapper<Orders> wrapper=new LambdaUpdateWrapper<>();
        wrapper.eq(Orders::getId,orders.getId());
        wrapper.set(Orders::getStatus,orders.getStatus());
        orderService.update(wrapper);
        return R.success("更改状态成功");
    }

}
