package com.wei.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wei.reggie.common.CustomException;
import com.wei.reggie.dto.SetmealDto;
import com.wei.reggie.entity.Setmeal;
import com.wei.reggie.entity.SetmealDish;
import com.wei.reggie.mapper.SetmealMapper;
import com.wei.reggie.service.CategoryService;
import com.wei.reggie.service.SetmealDishService;
import com.wei.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);

    }

    @Override
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态，是否具备删除的条件
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count=this.count(queryWrapper);
        //套餐必须是停用的才可以删除
        if(count>0){
            throw new CustomException("套餐在售卖状态，不能删除，手动停售即可删除");
        }
        this.removeByIds(ids);
        //查询与套餐关联的套餐菜品表的数据并删除
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lambdaQueryWrapper);
        log.info("在套餐-菜品对应表中与套餐关联的菜品数据删除成功");

    }
    @Transactional
    @Override
    public SetmealDto getSetmealDtoById(Long id) {
        SetmealDto setmealDto=new SetmealDto();
        Setmeal setmeal=this.getById(id);
        BeanUtils.copyProperties(setmeal,setmealDto);
        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(setmealDishes);
        Long categoryId=setmealDto.getCategoryId();
        String categoryName=categoryService.getById(categoryId).getName();
        setmealDto.setCategoryName(categoryName);
        return setmealDto;
    }
    @Transactional
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);
        log.info("接收要修改的套餐信息:"+setmealDto.toString());
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        List<SetmealDish> list = setmealDishes.stream().map((item) -> {
            item.setCreateTime(setmealDto.getCreateTime());
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(list);

    }
}
