package com.wei.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wei.reggie.dto.DishDto;
import com.wei.reggie.entity.Dish;
import com.wei.reggie.entity.DishFlavor;
import com.wei.reggie.mapper.DishMapper;
import com.wei.reggie.service.DishFlavorService;
import com.wei.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);
        log.info("保存菜品及口味:{}",dishDto.toString());
        setIdForEachFlavorAndSaveData(dishDto);
    }
    //根据id查询菜品和口味信息
    @Override
    public DishDto getThisById(Long id) {
        Dish dish = this.getById(id);//查到了菜品信息
        DishDto dishDto=new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        setIdForEachFlavorAndSaveData(dishDto);
    }
    @Transactional
    @Override
    public void removeWithFlavour(List<Long> ids) {
        for(int i=0;i<ids.size();i++) {
            LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishLambdaQueryWrapper.eq(Dish::getId,ids.get(i));
            this.remove(dishLambdaQueryWrapper);
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,ids.get(i));
            dishFlavorService.remove(dishFlavorLambdaQueryWrapper);
        }

    }

    //通过DishDto对象保存口味对象集合
    public void setIdForEachFlavorAndSaveData(DishDto dishDto){
        Long dishId=dishDto.getId();
        List<DishFlavor> flavors=dishDto.getFlavors();
        flavors=flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        //保存菜品口味到口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

}
