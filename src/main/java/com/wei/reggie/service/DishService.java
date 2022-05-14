package com.wei.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wei.reggie.dto.DishDto;
import com.wei.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应的口味数据，操作dish和dish_flavor两张表
    public void saveWithFlavor(DishDto dishDto);
    //根据id查询菜品和口味信息
    public DishDto getThisById(Long id);
    //更新菜品信息以及更新对应的口味信息
    void updateWithFlavor(DishDto dishDto);

    void removeWithFlavour(List<Long> ids);
}
