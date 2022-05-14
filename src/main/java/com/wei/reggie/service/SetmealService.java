package com.wei.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wei.reggie.dto.SetmealDto;
import com.wei.reggie.entity.Setmeal;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    //保存套餐及其对应菜品的关联关系
    @Transactional
    void saveWithDish(SetmealDto setmealDto);
    //删除套餐和菜品的关联关系
    void removeWithDish(List<Long> ids);

    SetmealDto getSetmealDtoById(Long id);

    void updateWithDish(SetmealDto setmealDto);
}
