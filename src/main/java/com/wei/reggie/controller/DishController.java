package com.wei.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wei.reggie.common.R;
import com.wei.reggie.dto.DishDto;
import com.wei.reggie.entity.Category;
import com.wei.reggie.entity.Dish;
import com.wei.reggie.entity.DishFlavor;
import com.wei.reggie.entity.Setmeal;
import com.wei.reggie.service.CategoryService;
import com.wei.reggie.service.DishFlavorService;
import com.wei.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

//菜品管理
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("新增菜品，接收参数:"+dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Dish> pageInfo=new Page<>(page,pageSize);
        //处理返回菜品分类名称的问题
        Page<DishDto> dtoPage=new Page<>(page,pageSize);
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Dish> records=pageInfo.getRecords();

        List<DishDto> list=records.stream().map((item)->{
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category=categoryService.getById(categoryId);
            if(category!=null){
                String categoryName=category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);

        return R.success(dtoPage);
    }
    //根据id查询菜品和口味信息
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getThisById(id);
        return R.success(dishDto);
    }
    //更新菜品的数据
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info("对菜品信息进行修改，原菜品:{}",dishDto.toString());
        dishService.updateWithFlavor(dishDto);

        return R.success("修改菜品成功");
    }
    //查找菜品列表信息
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        List<DishDto> dishDtoList=list.stream().map((item)->{
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String categoryName=category.getName();
                dishDto.setCategoryName(categoryName);
            }
            Long dishId=item.getId();
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper=new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDto.setFlavors(dishFlavors);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }
    @DeleteMapping
    public R<String> deleteDishes(@RequestParam List<Long> ids){
        log.info("删除或批量删除以下菜品: ids:{}",ids.toString());
        dishService.removeWithFlavour(ids);
        return R.success("删除成功");
    }
    @PostMapping("/status/0")
    public R<String> banSell(@RequestParam List<Long> ids){
        log.info("停售或批量停售该菜品，其ids为{}",ids.toString());
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        List<Dish> dishes = dishService.list(queryWrapper);
        List<Dish> collect = dishes.stream().map((item) -> {
            item.setStatus(0);
            return item;
        }).collect(Collectors.toList());
        dishService.updateBatchById(collect);
        return R.success("禁售成功!");
    }
    @PostMapping("/status/1")
    public R<String> recoverySell(@RequestParam List<Long> ids){
        log.info("启售或批量启售该菜品，其ids为{}",ids.toString());
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        List<Dish> dishes = dishService.list(queryWrapper);
        List<Dish> collect = dishes.stream().map((item) -> {
            item.setStatus(1);
            return item;
        }).collect(Collectors.toList());
        dishService.updateBatchById(collect);
        return R.success("启售成功!");
    }

    
}
