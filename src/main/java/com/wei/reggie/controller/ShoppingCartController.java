package com.wei.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wei.reggie.common.BaseContext;
import com.wei.reggie.common.R;
import com.wei.reggie.entity.Setmeal;
import com.wei.reggie.entity.ShoppingCart;
import com.wei.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车数据:"+shoppingCart.toString());
        Long currentId= BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        //查询当前菜品或套餐是否已在购物车内
        Long dishId=shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        if(dishId!=null){
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }
        else{
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //从数据库中查找用户是否已选择过这款菜品或套餐，存在就数量加1
        ShoppingCart oldCart = shoppingCartService.getOne(queryWrapper);
        if(oldCart!=null){
            Integer number=oldCart.getNumber();
            oldCart.setNumber(number+1);
            shoppingCartService.updateById(oldCart);
        }else {
            shoppingCartService.save(shoppingCart);
            oldCart=shoppingCart;
        }
        return R.success(oldCart);
    }
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车...");
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }
    @PostMapping("/sub")
    public R<String> deleteItem(@RequestBody ShoppingCart shoppingCart){
        Long currentId= BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        Long dishId = shoppingCart.getDishId();
        if(dishId!=null){
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
            ShoppingCart cartItem = shoppingCartService.getOne(queryWrapper);
            Integer number = cartItem.getNumber();
            if(number>1){
                cartItem.setNumber(number-1);
                shoppingCartService.updateById(cartItem);
            }else {
                shoppingCartService.remove(queryWrapper);
            }
        }else{
            Long setmealId = shoppingCart.getSetmealId();
            queryWrapper.eq(ShoppingCart::getSetmealId,setmealId);
            ShoppingCart cartItem=shoppingCartService.getOne(queryWrapper);
            Integer number = cartItem.getNumber();
            if(number>1){
                cartItem.setNumber(number-1);
                shoppingCartService.updateById(cartItem);
            }else {
                shoppingCartService.remove(queryWrapper);
            }
        }
        return R.success("删除该商品成功");
    }
    @DeleteMapping("/clean")
    public R<String> clean(){
        Long currentId= BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        shoppingCartService.remove(queryWrapper);
        return R.success("成功清空购物车");
    }
}
