package com.wei.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wei.reggie.common.R;
import com.wei.reggie.dto.SetmealDto;
import com.wei.reggie.entity.Category;
import com.wei.reggie.entity.Setmeal;
import com.wei.reggie.service.CategoryService;
import com.wei.reggie.service.SetmealDishService;
import com.wei.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;
    @PostMapping
    @CacheEvict(value="setmealCache",allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("新增套餐，接收前端的套餐Dto数据:{}",setmealDto.toString());
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Setmeal> pageInfo=new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage=new Page<>();
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, queryWrapper);
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list= records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
                BeanUtils.copyProperties(item, setmealDto);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }
    @DeleteMapping
    @CacheEvict(value="setmealCache",allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("删除或批量删除以下套餐: ids:{}",ids.toString());
        setmealService.removeWithDish(ids);
        return R.success("套餐数据删除成功");
    }
    @PostMapping("/status/0")
    public R<String> banSell(@RequestParam List<Long> ids){
        log.info("停售或批量停售该套餐，其ids为{}",ids.toString());
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        List<Setmeal> setmeals = setmealService.list(queryWrapper);
        setmeals.stream().map((item)->{
            item.setStatus(0);
            return item;
        }).collect(Collectors.toList());
        setmealService.updateBatchById(setmeals);
        return R.success("禁售成功!");
    }
    @PostMapping("/status/1")
    public R<String> recoverySell(@RequestParam List<Long> ids){
        log.info("启售或批量启售该套餐，其ids为{}",ids.toString());
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        List<Setmeal> setmeals = setmealService.list(queryWrapper);
        setmeals.stream().map((item)->{
            item.setStatus(1);
            return item;
        }).collect(Collectors.toList());
        setmealService.updateBatchById(setmeals);
        return R.success("启售成功!");
    }
    //回显套餐数据
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getSetmealDtoById(id);
        log.info("回显套餐信息:"+setmealDto.toString());
        return R.success(setmealDto);
    }
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return R.success("修改套餐信息成功");
    }
    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key="#setmeal.categoryId+'_'+#setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = setmealService.list(queryWrapper);
        return R.success(setmealList);
    }


}
